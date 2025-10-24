# server.py
from flask import Flask, request, jsonify
from flask_cors import CORS
import tempfile, os
from pydub import AudioSegment
import soundfile as sf
import numpy as np
import difflib
import torch
from transformers import Wav2Vec2ForCTC, Wav2Vec2Processor

app = Flask(__name__)
CORS(app)

# -----------------------------
# Load Wav2Vec2 model once
# -----------------------------
print("🔄 Loading Wav2Vec2 model...")
processor = Wav2Vec2Processor.from_pretrained("facebook/wav2vec2-base-960h")
model = Wav2Vec2ForCTC.from_pretrained("facebook/wav2vec2-base-960h")
model.eval()
print("✅ Model loaded successfully!")

# -----------------------------
# Helper function for scoring
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
        return "🎯 Good effort! Work on clarity and pace."
    else:
        return "🗣 Keep practicing — speak slowly and clearly, focus on pronunciation."

# -----------------------------
# /analyze route
# -----------------------------
@app.route("/analyze", methods=["POST"])
def analyze():
    temp_wav_path = None
    temp_wav_mono = None
    try:
        # 1️⃣ Validate request
        if 'audio' not in request.files:
            return jsonify({"error": "No audio file uploaded"}), 400
        if 'expected_text' not in request.form:
            return jsonify({"error": "Missing expected_text"}), 400

        expected_text = request.form['expected_text'].strip().lower()
        audio_file = request.files['audio']

        if audio_file.filename == '':
            return jsonify({"error": "Empty filename"}), 400

        # 2️⃣ Save audio to temp file
        with tempfile.NamedTemporaryFile(delete=False, suffix=".wav") as tmp:
            temp_wav_path = tmp.name
            audio_file.save(temp_wav_path)

        # 3️⃣ Convert to mono WAV 16kHz
        audio = AudioSegment.from_file(temp_wav_path)
        audio = audio.set_channels(1).set_frame_rate(16000)
        temp_wav_mono = temp_wav_path + "_mono.wav"
        audio.export(temp_wav_mono, format="wav")

        # 4️⃣ Load audio
        speech, rate = sf.read(temp_wav_mono)
        if len(speech.shape) > 1:
            speech = np.mean(speech, axis=1)

        # 5️⃣ Transcribe using Wav2Vec2
        input_values = processor(speech, return_tensors="pt", sampling_rate=rate).input_values
        with torch.no_grad():
            logits = model(input_values).logits
        predicted_ids = torch.argmax(logits, dim=-1)
        transcription = processor.decode(predicted_ids[0]).lower().strip()

        # 6️⃣ Compute scores
        pronunciation_score = compute_pronunciation_score(expected_text, transcription)
        fluency_score = compute_fluency_score(transcription, expected_text)
        tips = generate_tips(pronunciation_score, fluency_score)

        # 7️⃣ Return JSON response
        return jsonify({
            "status": "success",
            "expected_text": expected_text,
            "transcript": transcription,
            "pronunciation_score": pronunciation_score,
            "fluency_score": fluency_score,
            "tips": tips
        })

    except Exception as e:
        print(f"❌ Error in /analyze: {str(e)}")
        return jsonify({"error": str(e)}), 500

    finally:
        # 8️⃣ Cleanup temp files
        for path in [temp_wav_path, temp_wav_mono]:
            if path and os.path.exists(path):
                os.remove(path)

# -----------------------------
# Run server
# -----------------------------
if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)
