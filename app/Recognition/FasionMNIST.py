import tensorflow as tf
import sys
import numpy as np
import shutil
import os
from tensorflow.keras import layers
data = []
labels = []

def parser(record):
    keys_to_features = {
        "image_raw": tf.FixedLenFeature([], tf.string),
        "label":     tf.FixedLenFeature([], tf.int64)
    }
    parsed = tf.parse_single_example(record, keys_to_features)
    image = tf.decode_raw(parsed["image_raw"], tf.uint8)
    image.set_shape([100*100])
    image = tf.cast(image, tf.float32) * (1. / 255) - 0.5
    label = parsed["label"]
    return image, label

def input_fn(filenames, train, batch_size=32, buffer_size=2048):
    dataset = tf.data.TFRecordDataset(filenames=filenames)
    dataset = dataset.map(parser)

    if train:
        dataset = dataset.shuffle(buffer_size=buffer_size)
        num_repeat = None
    else:
        num_repeat = 1

    dataset = dataset.repeat(num_repeat)
    dataset = dataset.batch(batch_size)

    # Create an iterator for the dataset and the above modifications.
    iterator = dataset.make_one_shot_iterator()

    # Get the next batch of images and labels.
    images_batch, labels_batch = iterator.get_next()

    # The input-function must return a dict wrapping the images.
    x = {'image': images_batch}
    y = labels_batch

    return x, y

def train_input_fn():
    return input_fn(filenames="C:\\Users\\jimmy\\source\\repos\\image_to_TFRecord\\image_to_TFRecord\\TFREcord\\train.tfrecords", train=True)

def test_input_fn():
    return input_fn(filenames="C:\\Users\\jimmy\\source\\repos\\image_to_TFRecord\\image_to_TFRecord\\TFREcord\\test.tfrecords", train=False)

def delete_old_model():
    myfile="DNN_Model"
    if os.path.isdir(myfile):
        shutil.rmtree(myfile)

#delete_old_model()

feature_image = tf.feature_column.numeric_column("image", shape=[100,100,1])
feature_columns = [feature_image]
num_hidden_units = [256, 128]

model = tf.estimator.DNNClassifier(feature_columns=feature_columns,
                                   hidden_units=num_hidden_units,
                                   optimizer=tf.train.AdamOptimizer(1e-4),
                                   dropout=0.1,
                                   n_classes=5,
                                   model_dir="DNN_Model")
count = 0
while (count < 5000):
    model.train(input_fn=train_input_fn, steps = 1000)
    result = model.evaluate(input_fn=test_input_fn)
    print("Classification accuracy: {0:.2%}".format(result["accuracy"]))
    sys.stdout.flush()
    count +=1

