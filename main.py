

from flask import Flask, request
import os
from googleapiclient.discovery import build
import json

app = Flask(__name__)

api_key = 'AIzaSyCnCwf-1gIzU0NjwtEL5ItILqlGlH659XI'
cse_id = '013673076675552337852:qdyrlcmlyli'


class Item:

    def __init__(self, title, link):
        self.title = title
        self.link = link

    def __repr__(self):
        return "title:{0}~link:{1}".format(self.title, self.link)


def google_search(search_term, api_key, cse_id, **kwargs):
    service = build('customsearch', 'v1', developerKey=api_key)
    res = service.cse().list(q=search_term, cx=cse_id, **kwargs).execute()['items']
    items = []
    response = []
    for item in res:
        items.append(Item(item['title'], item['link']))

    for item in items:
        response.append(item.__repr__() + '\n')

    return ''.join(response)


@app.route('/picture', methods = ['POST', 'GET'])
def test():
    image_path = "demofile2.jpg"
    result_path = 'results.txt'
    graph_path = 'tf_files/retrained_graph.pb'

    file = request.files['pic']
    f = open(image_path, "wb")
    for line in file:
        f.write(line)
    f.close()

    command = 'python -m scripts.label_image --graph=' + graph_path + ' --image=' + image_path + ' > ' + result_path
    os.system(command)
    result_file = open(result_path, 'r')
    prediction = str(result_file.read())
    return prediction



@app.route('/getResults', methods = ['POST', 'GET'])
def getResults():
    clothes = request.form.get('choice')
    return google_search(clothes, api_key, cse_id, num=10)


app.run()
