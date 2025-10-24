import random
import json
import os
import pyttsx3
import sounddevice as sd
import wavio
from vosk import Model, KaldiRecognizer
import wave
import json as js
import librosa
import numpy as np
from transformers import AutoTokenizer, AutoModelForSeq2SeqLM, pipeline

# ---------------------------
# 1. Load random question
# ---------------------------
with open("questions.json", "r") as f:
    questions = json.load(f)

question = random.choice(questions)
print("Question:", question)

# ---------------------------
# 2. TTS to speak question
# ---------------------------
engine = pyttsx3.init()
engine.say(question)
engine.runAndWait()

# ---------------------------
# 3. Record voice
# ---------------------------
fs = 16000  # Sampling rate
seconds = 7  # Duration of recording
audio_folder = "audio"
os.makedirs(audio_folder, exist_ok=True)
audio_path = os.path.join(audio_folder, "response.wav")

print("🎙️ Recording... Speak now!")
recording = sd.rec(int(seconds * fs), samplerate=fs, channels=1, dtype='int16')
sd.wait()
wavio.write(audio_path, recording, fs, sampwidth=2)
print("✅ Recording saved.")

# ---------------------------
# 4. Speech-to-Text (Vosk)
# ---------------------------
vosk_model_path = "models/vosk-model-small-en-us-0.15"
model = Model(vosk_model_path)
rec = KaldiRecognizer(model, fs)

wf = wave.open(audio_path, "rb")
text_output = ""
while True:
    data = wf.readframes(4000)
    if len(data) == 0:
        break
    if rec.AcceptWaveform(data):
        res = js.loads(rec.Result())
        text_output += " " + res.get("text", "")
# Final chunk
res = js.loads(rec.FinalResult())
text_output += " " + res.get("text", "")

text_output = text_output.strip()
print("\n🗣️ You said:", text_output if text_output else "Nothing spoken")

# ---------------------------
# 5. Grammar Correction Model (prithivida)
# ---------------------------
print("\n⏳ Loading grammar correction model (first time may take a few minutes)...")
grammar_model_name = "prithivida/grammar_error_correcter_v1"
grammar_tokenizer = AutoTokenizer.from_pretrained(grammar_model_name)
grammar_model = AutoModelForSeq2SeqLM.from_pretrained(grammar_model_name)
grammar_pipe = pipeline("text2text-generation", model=grammar_model, tokenizer=grammar_tokenizer)

# ---------------------------
# 6. Tips Generation Model (Flan-T5-small)
# ---------------------------
tips_model_name = "google/flan-t5-base"
tips_tokenizer = AutoTokenizer.from_pretrained(tips_model_name)
tips_model = AutoModelForSeq2SeqLM.from_pretrained(tips_model_name)
tips_pipe = pipeline("text2text-generation", model=tips_model, tokenizer=tips_tokenizer)

# ---------------------------
# 7. Functions for Correction and Tips
# ---------------------------
def correct_sentence(text):
    prompt = f"grammar: {text}"
    result = grammar_pipe(prompt, max_length=256)[0]['generated_text']
    return result

def generate_tips(text):
    prompt = (
        "You are an English teacher. "
        "Provide 2-3 short tips to improve English speaking based on this sentence. "
        "Include advice on pronunciation, fluency, and style. "
        "Even if the sentence is correct, give useful tips. "
        f"Sentence: '{text}'"
    )
    result = tips_pipe(prompt, max_length=128)[0]['generated_text']

    # If model just repeats input, fallback to generic tips
    if text.lower() in result.lower() or result.strip() == "":
        return "Speak clearly and confidently. Practice smooth transitions between words. Focus on correct pronunciation."
    
    return result

# ---------------------------
# 8 & 9. Handle empty speech, Fluency Analysis, and Feedback
# ---------------------------
if not text_output:
    # Case when nothing was spoken
    corrected_sentence = "Nothing spoken"
    tips = "Nothing spoken"
    fluency_score = 0
else:
    # Grammar correction
    corrected_sentence = correct_sentence(text_output)

    # Tips generation
    tips = generate_tips(text_output)

    # ---------------------------
    # Fluency Analysis (Librosa)
    # ---------------------------
    y, sr = librosa.load(audio_path, sr=fs)
    duration = librosa.get_duration(y=y, sr=sr)

    # Speech rate
    words = len(text_output.split())
    speech_rate = words / duration  # words per second

    # Pause detection
    intervals = librosa.effects.split(y, top_db=30)
    active_duration = sum((end-start)/sr for start, end in intervals)
    pause_ratio = 1 - active_duration/duration

    # Scoring
    pause_score = (1 - pause_ratio) * 100
    speech_rate_score = min(max(speech_rate * 20, 0), 100)
    fluency_score = 0.5 * pause_score + 0.5 * speech_rate_score

# ---------------------------
# 10. Final Feedback
# ---------------------------
print("\n--- FEEDBACK ---")
print(f"✅ Corrected Sentence: {corrected_sentence}")
print(f"💡 Speaking Tips: {tips}")
print(f"🗣️ Fluency Score: {fluency_score:.2f}/100")

# ---------------------------
# 11. TTS Final Feedback
# ---------------------------
final_feedback = f"Your corrected sentence is: {corrected_sentence}. Your fluency score is {fluency_score:.2f} out of 100. Here are some tips: {tips}"
engine.say(final_feedback)
engine.runAndWait()
