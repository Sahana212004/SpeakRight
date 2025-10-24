import random
import json
import os
import pyttsx3
import sounddevice as sd
import wavio
from vosk import Model, KaldiRecognizer
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
seconds = 7  # Duration
audio_folder = "audio"
os.makedirs(audio_folder, exist_ok=True)
audio_path = os.path.join(audio_folder, "response.wav")

print("Recording... Speak now!")
recording = sd.rec(int(seconds * fs), samplerate=fs, channels=1, dtype='int16')
sd.wait()
wavio.write(audio_path, recording, fs, sampwidth=2)
print("Recording saved.")

# ---------------------------
# 4. Speech-to-Text (Vosk)
# ---------------------------
vosk_model_path = "models/vosk-model-small-en-us-0.15"
model = Model(vosk_model_path)
rec = KaldiRecognizer(model, fs)

import wave
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

print("You said:", text_output.strip())

# ---------------------------
# 5. ML-based Grammar & Tips (Flan-T5)
# ---------------------------
tokenizer = AutoTokenizer.from_pretrained("google/flan-t5-small")
model = AutoModelForSeq2SeqLM.from_pretrained("google/flan-t5-small")
generator = pipeline('text2text-generation', model=model, tokenizer=tokenizer)

prompt = f"You are an English teacher. Correct the grammar and structure of this sentence and explain the corrections: {text_output.strip()}"
tips = generator(prompt, max_new_tokens=150)
corrected_text = tips[0]['generated_text']
print("\nCorrected & Tips from Flant5 model :")
print(corrected_text)


# ---------------------------
# 5.1 ML-based Grammar & Tips (vennify-t5)
# ---------------------------
print("Loading grammar correction model...")
model_name = "vennify/t5-base-grammar-correction"  # or try prithivida/grammar_error_correcter_v1
tokenizer = AutoTokenizer.from_pretrained(model_name)
model = AutoModelForSeq2SeqLM.from_pretrained(model_name)

grammar_corrector = pipeline("text2text-generation", model=model, tokenizer=tokenizer)

def correct_sentence(text_output):
    prompt = f"fix grammar: {text_output}"
    corrected = grammar_corrector(prompt, max_length=256)[0]['generated_text']
    return corrected

def generate_tips(text_output):
    prompt = f"Give short English speaking improvement tips for: '{text_output}'"
    tips = grammar_corrector(prompt, max_length=128)[0]['generated_text']
    return tips



# ---------------------------
# 6. Fluency Analysis (Librosa)
# ---------------------------
y, sr = librosa.load(audio_path, sr=fs)
duration = librosa.get_duration(y=y, sr=sr)

# 1️⃣ Speech rate
words = len(text_output.strip().split())
speech_rate = words / duration  # words per second

# Pause detection
intervals = librosa.effects.split(y, top_db=30)
active_duration = sum((end-start)/sr for start,end in intervals)
pause_ratio = 1 - active_duration/duration

# Scores normalized 0-100
pause_score = (1 - pause_ratio) * 100
speech_rate_score = min(max(speech_rate * 20, 0), 100)  # adjust multiplier

# Final weighted fluency score
fluency_score = 0.5 * pause_score + 0.5 * speech_rate_score

print(f"Fluency Score: {fluency_score:.2f}/100")

# ---------------------------
# 7. Final Feedback (print & TTS)
# ---------------------------
corrected_text1 = correct_sentence(text_output)
tips = generate_tips(text_output)

print(f"Corrected Sentence: {corrected_text1}")
print(f"Tips for Improvement: {tips}")

final_feedback = f"Corrected Sentence & Tips from flan-t5: {corrected_text}\nFluency Score: {fluency_score:.2f}/100"
print("\n--- Final Feedback ---")
print(final_feedback)
print("\n")
print(f"Corrected Sentence from venify t5: {corrected_text1} \n")
print(f"Tips for Improvement: {tips}")

engine.say(final_feedback)
engine.runAndWait()
