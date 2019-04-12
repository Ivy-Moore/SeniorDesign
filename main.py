from flask import Flask, request
import os
# from googlesearch import search

app = Flask(__name__)


@app.route('/test' ,methods = ['POST', 'GET'])
def test():
    file = request.files['pic']
    # print(file.read())
    f = open("demofile2.jpg", "wb")
    for line in file: 
        f.write(line)
    f.close()
    

    dir = os.getcwd()


    image_path = 'jeans_test.jpg'
    result_path = 'results.txt'
    graph_path = 'tf_files/retrained_graph.pb'
    # command = 'python -m scripts.label_image --graph=' + graph_path + ' --image=' + image_path + ' > ' + result_path
    # os.system(command)
    # result_file = open(result_path, 'r')
    # prediction = str(result_file.read()).split()[0]
    # search_result = search(prediction, tld='com', lang='en', num=10, start=0, stop=None, pause=2.0)
    # return search_result
    return "hello" 


app.run()
