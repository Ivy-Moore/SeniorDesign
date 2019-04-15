from flask import Flask, request
import os
from googleapiclient.discovery import build
import json


api_key = 'AIzaSyCnCwf-1gIzU0NjwtEL5ItILqlGlH659XI'
cse_id = '013673076675552337852:qdyrlcmlyli'

class Item:

    def __init__(self, title, link):
        self.title = title
        self.link = link

    def __repr__(self):
        return "title:{0},link:{1}".format(self.title, self.link)


def google_search(search_term, api_key, cse_id, **kwargs):
    service = build('customsearch', 'v1', developerKey=api_key)
    res = service.cse().list(q=search_term, cx=cse_id, **kwargs).execute()['items']
    result_file = open("requirements.txt", 'r')
    print(str(result_file.read()))
    result_file.close()
    items = []
    response = []
    for item in res:
        items.append(Item(item['title'], item['link']))
    
    for item in items:
        response.append(item.__repr__() + '\n')
    
    return ''.join(response)


google_search("jeans", api_key, cse_id, num=3)