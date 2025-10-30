from flask import Flask, request, jsonify
from flask_cors import CORS
from pydub import AudioSegment
import tempfile, os
import soundfile as sf
import numpy as np
import difflib
import torch
from transformers import Wav2Vec2ForCTC, Wav2Vec2Processor

app = Flask(__name__)
CORS(app)

# -----------------------------
# Load Wav2Vec2 model
# -----------------------------
print("🔄 Loading Wav2Vec2 model...")
processor = Wav2Vec2Processor.from_pretrained("facebook/wav2vec2-base-960h")
model = Wav2Vec2ForCTC.from_pretrained("facebook/wav2vec2-base-960h")
model.eval()
print("✅ Model loaded successfully!")

# -----------------------------
# Helper scoring functions
# -----------------------------
def compute_pronunciation_score(expected_text, transcription):
    return round(difflib.SequenceMatcher(None, expected_text, transcription).ratio() * 100, 2)

def compute_fluency_score(transcription, expected_text):
    word_ratio = len(transcription.split()) / max(1, len(expected_text.split()))
    return round(min(100, word_ratio * 100), 2)

def generate_tips(pron_score, fluency_score):
    if pron_score > 90:
        return "🌟 Excellent pronunciation and fluency!"
    elif pron_score > 70:
        return "🎯 Good effort! Try to speak more smoothly."
    else:
        return "🗣 Keep practicing — speak slowly and clearly, focus on pronunciation."

# -----------------------------
# /analyze route
# -----------------------------
@app.route("/analyze", methods=["POST"])
def analyze():
    temp_in = None
    temp_out = None
    try:
        if "audio" not in request.files:
            return jsonify({"error": "No audio file uploaded"}), 400
        if "text" not in request.form:
            return jsonify({"error": "Missing text"}), 400

        expected_text = request.form["text"].strip().lower()
        file = request.files["audio"]

        # Save uploaded file
        temp_in = tempfile.NamedTemporaryFile(delete=False, suffix=".m4a").name
        file.save(temp_in)

        # Convert to mono 16kHz WAV
        temp_out = tempfile.NamedTemporaryFile(delete=False, suffix=".wav").name
        audio = AudioSegment.from_file(temp_in)
        audio = audio.set_channels(1).set_frame_rate(16000)
        audio.export(temp_out, format="wav")

        # Load and transcribe
        speech, sr = sf.read(temp_out)
        if len(speech.shape) > 1:
            speech = np.mean(speech, axis=1)

        inputs = processor(speech, return_tensors="pt", sampling_rate=sr).input_values
        with torch.no_grad():
            logits = model(inputs).logits
        pred_ids = torch.argmax(logits, dim=-1)
        transcription = processor.decode(pred_ids[0]).lower().strip()

        # Scores
        pron_score = compute_pronunciation_score(expected_text, transcription)
        fluency_score = compute_fluency_score(transcription, expected_text)
        grammar_score = round((pron_score + fluency_score) / 2, 2)
        total_score = round((pron_score + fluency_score + grammar_score) / 3, 2)
        feedback = generate_tips(pron_score, fluency_score)

        return jsonify({
            "recognized_text": transcription,
            "pronunciation_score": pron_score,
            "fluency_score": fluency_score,
            "grammar_score": grammar_score,
            "total_score": total_score,
            "feedback": feedback
        })

    except Exception as e:
        print(f"❌ Error: {str(e)}")
        return jsonify({"error": str(e)}), 500
    finally:
        for f in [temp_in, temp_out]:
            if f and os.path.exists(f):
                os.remove(f)

# -----------------------------
if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)
