# -*- coding: utf-8 -*-

import requests
import json
import urllib3
import datetime

from flask import Flask, request, make_response, jsonify, render_template
from bs4 import BeautifulSoup
from urllib.request import urlopen

ERROR_MESSAGE = '네트워크 접속에 문제가 발생하였습니다. 잠시 후 다시 시도해주세요.'
URL_OPEN_TIME_OUT = 10

app = Flask(__name__)


# ----------------------------------------------------
# Dialogflow에서 대답 구함
# ----------------------------------------------------
def get_answer(text, user_key):
    # --------------------------------
    # Dialogflow에 요청
    # --------------------------------
    data_send = {
        'lang': 'ko',
        'query': text,
        'sessionId': user_key,
        'timezone': 'Asia/Seoul'
    }

    data_header = {
        'Content-Type': 'application/json; charset=utf-8',
        'Authorization': 'b5a055ab32ed4e3b8f4a1a5bb54701fb  '  # Dialogflow의 Client access token 입력
    }

    dialogflow_url = 'https://api.dialogflow.com/v1/query?v=20150910'

    res = requests.post(dialogflow_url,
                        data=json.dumps(data_send),
                        headers=data_header)

    # --------------------------------
    # 대답 처리
    # --------------------------------
    if res.status_code != requests.codes.ok:
        return ERROR_MESSAGE

    data_receive = res.json()
    answer = data_receive['result']['fulfillment']['speech']

    return answer


# ----------------------------------------------------
# 이동경로 처리(진행중)
# ----------------------------------------------------
def Route(now, destination):
    if now == u'신공학관' and destination == u'학생회관':
        answer = u'신공학관 7층으로 가신 후 바깥 통로에서 ROTC관으로 가는 길 통해 원흥관으로 간 뒤'
        answer += '입구로 나와 정보문화관p 1층을 통해 나가시면됩니다.'

    elif now == u'' and destination == u'':
        answer = u''

    return answer


# ----------------------------------------------------
# 학식 메뉴 처리
# ----------------------------------------------------
def Menu(address):
    html = urlopen('http://dgucoop.dongguk.edu/store/store.php?w=4&l=2&j=0')
    source = html.read()
    html.close()

    n = datetime.datetime.today().weekday()

    soup = BeautifulSoup(source, "lxml")
    table_div = soup.find(id="sdetail")
    tables = table_div.find_all("table")

    menu_table = tables[1]

    trs = menu_table.find_all('tr')

    if address == u'상록원':

        ilpoom_lunch = trs[10]
        ilpoom_dinner = trs[11]

        ilpLn = ilpoom_lunch.find_all('td')
        if not ilpLn[n + 3].span.get_text():
            ilp_menu = '일품코너 중식 : 없음, 석식 : '
        else:
            ilp_menu = '일품코너 중식 : ' + ilpLn[n + 3].span.get_text() + ', 석식 : '

        ilpDn = ilpoom_dinner.find_all('td')
        if not ilpDn[n + 2].span.get_text():
            ilp_menu += '없음 \n'
        else:
            ilp_menu += ilpDn[n + 2].span.get_text() + '\n'

        western_Lunch = trs[12]
        western_Dinner = trs[13]

        wstLn = western_Lunch.find_all('td')
        if not wstLn[n + 3].span.get_text():
            wst_menu = '양식코너 중식 : 없음, 석식 : '
        else:
            wst_menu = '양식코너 중식 : ' + wstLn[n + 3].span.get_text() + ', 석식 : '

        wstDn = western_Dinner.find_all('td')
        if not wstDn[n + 2].span.get_text():
            wst_menu += '없음 \n'
        else:
            wst_menu += wstDn[n + 2].span.get_text() + '\n'

        ddk_Lunch = trs[14]
        ddk_Dinner = trs[15]

        ddkLn = ddk_Lunch.find_all('td')
        if not ddkLn[n + 3].span.get_text():
            ddk_menu = '뚝배기 코너 중식 : 없음, 석식 : '
        else:
            ddk_menu = '뚝배기코너 중식 : ' + ddkLn[n + 3].span.get_text() + ', 석식 : '

        ddkDn = ddk_Dinner.find_all('td')
        if not ddkDn[n + 2].span.get_text():
            ddk_menu += '없음 \n'
        else:
            ddk_menu += ddkDn[n + 2].span.get_text() + '\n'

        answer = '상록원 메뉴정보입니다.맛있는 식사 되세요! \n' + ilp_menu + wst_menu + ddk_menu

    elif address == u'그루터기':

        A_lunch = trs[25]
        A_dinner = trs[26]

        ALn = A_lunch.find_all('td')
        if not ALn[n + 3].span.get_text():
            A_menu = 'A코너 중식 : 없음, 석식 : '
        else:
            A_menu = 'A코너 중식 : ' + ALn[n + 3].span.get_text() + ', 석식 : '

        ADn = A_dinner.find_all('td')
        if not ADn[n + 2].span.get_text():
            A_menu += '없음 \n'
        else:
            A_menu += ADn[n + 2].span.get_text() + '\n'

        B_lunch = trs[27]
        B_dinner = trs[28]

        BLn = B_lunch.find_all('td')
        if not BLn[n + 3].span.get_text():
            B_menu = 'B코너 중식 : 없음, 석식 : '
        else:
            B_menu = 'B코너 중식 : ' + ALn[n + 3].span.get_text() + ', 석식 : '

        BDn = B_dinner.find_all('td')
        if not ADn[n + 2].span.get_text():
            B_menu += '없음 \n'
        else:
            B_menu += ADn[n + 2].span.get_text() + '\n'

        answer = '그루터기 메뉴 정보입니다. 맛있게 드세요! \n' + A_menu + B_menu

    elif address == u'기숙사식당':

        Morning = trs[40]
        A_lunch = trs[41]
        A_dinner = trs[42]

        Mor = Morning.find_all('td')
        if not Mor[n + 2].span.get_text():
            Dorm_menu = '기숙사식당 조식  : 없음, 중식 : '
        else:
            Dorm_menu = '기숙사식당 조식 : ' + Mor[n + 2].span.get_text() + ', 중식 : '

        ALn = A_lunch.find_all('td')
        if not ALn[n + 3].span.get_text():
            Dorm_menu += '없음, 석식 : '
        else:
            Dorm_menu += ALn[n + 3].span.get_text() + ', 석식 : '

        ADn = A_dinner.find_all('td')
        if not ADn[n + 2].span.get_text():
            Dorm_menu += '없음 \n'
        else:
            Dorm_menu += ADn[n + 2].span.get_text() + '\n'

        answer = '기숙사식당 메뉴 정보입니다. 맛있게 드세요! \n' + Dorm_menu
    return answer


# ------------------------------------'/'
# Dialogflow fullfillment 처리
# ------------------------------------'/'
@app.route('/', methods=['POST'])
def webhook():
    # --------------------------------'/'
    # 액션 구함
    # --------------------------------
    req = request.get_json(force=True)
    action = req['result']['action']

    # --------------------------------
    # 액션 처리
    # --------------------------------
    if action == 'CafeterriaLocation':
        address = req['result']['parameters']['dining_place']
        answer = Menu(address)
    elif action == 'Route':
        now = req['result']['parameters']['DG_Locate2']
        address = req['result']['parameters']['DG_Locate']
        answer = Route(pizza_name, address)
    else:
        answer = 'error'

    res = {'speech': answer}

    return jsonify(res)


# ----------------------------------------------------
# 메인 함수
# ----------------------------------------------------
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, threaded=True)