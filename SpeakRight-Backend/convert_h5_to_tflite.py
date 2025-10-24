import tensorflow as tf

# load Keras model (.h5)
model = tf.keras.models.load_model("our_model.h5")

# OPTIONAL: print shapes so you know what Android must feed
print("INPUT SHAPE:", model.input_shape)
print("OUTPUT SHAPE:", model.output_shape)

# create converter
converter = tf.lite.TFLiteConverter.from_keras_model(model)

# OPTIONAL: enable optimizations (smaller/ faster)
# converter.optimizations = [tf.lite.Optimize.DEFAULT]

# convert
tflite_model = converter.convert()

# save
with open("our_model.tflite", "wb") as f:
    f.write(tflite_model)

print("Saved our_model.tflite")
