# download_dataset.py
from datasets import load_dataset
import librosa
import os

# Step 1: Load Speechocean762 dataset (no audio decoding)
dataset = load_dataset("mispeech/speechocean762")
print("Dataset loaded successfully!")
print(dataset)

# Step 2: Access first sample WITHOUT triggering audio decoding
# PyArrow table slice → to_pydict returns dict of lists
row0 = dataset["train"].data.slice(0, 1).to_pydict()

print("\nKeys in the first row:", list(row0.keys()))

# Step 3: Get audio path
audio_info = row0['audio'][0]  # audio column stores dicts
audio_path = audio_info['path']
print("Audio file path:", audio_path)

# Step 4: Load audio manually with librosa
waveform, sr = librosa.load(audio_path, sr=None)
print("Loaded waveform shape:", waveform.shape)
print("Sample rate:", sr)

# Step 5: Print pronunciation scores
scores = {
    "accuracy": row0['accuracy'][0],
    "fluency": row0['fluency'][0],
    "completeness": row0['completeness'][0],
    "prosodic": row0['prosodic'][0],
    "total": row0['total'][0]
}
print("\nPronunciation scores for first sample:", scores)

# Optional: Function to load any sample by index
def load_sample(index):
    sample_row = dataset["train"].data.slice(index, 1).to_pydict()
    audio_path = sample_row['audio'][0]['path']
    waveform, sr = librosa.load(audio_path, sr=None)
    scores = {
        "accuracy": sample_row['accuracy'][0],
        "fluency": sample_row['fluency'][0],
        "completeness": sample_row['completeness'][0],
        "prosodic": sample_row['prosodic'][0],
        "total": sample_row['total'][0]
    }
    text = sample_row['text'][0]
    return waveform, sr, scores, text

# Example: load 2nd sample
waveform2, sr2, scores2, text2 = load_sample(1)
print("\nSecond sample text:", text2)
print("Second sample scores:", scores2)
