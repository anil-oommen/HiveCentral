import { Injectable } from '@angular/core';
//import * as Rx from 'rxjs/Rx';
//import { Observable,Subject,Observer }   from 'rxjs';
//import { Rx }   from 'rxjs/Rx';
import Stomp from 'stompjs';
import SockJS from 'sockjs-client';


@Injectable()
export class AppWebsockService { 

    private stompClient;
    private serverUrl = 'http://localhost:8080/socket';
    constructor() { 
        this.initializeWebSocketConnection();
    }


    initializeWebSocketConnection(){
        //let ws = new SockJS(this.serverUrl);
        //new SockJS()
            //"http://192.168.1.103:8080//websocket-discovery");
        /*this.stompClient = Stomp.over(ws);
        let that = this;
        this.stompClient.connect({}, function(frame) {
          that.stompClient.subscribe("/websocket/topic/notify", (message) => {
            if(message.body) {
              console.log(message.body);
            }
          });
        });
        */
      }
    
      sendMessage(message){
        this.stompClient.send("/websocket/topic/message" , {}, message);
      }

  /*
  private subject: Subject<MessageEvent>;

  public connect(url): Subject<MessageEvent> {
    if (!this.subject) {
      this.subject = this.create(url);
      console.log("Successfully connected: " + url);
    } 
    return this.subject;
  }

  private create(url): Subject<MessageEvent> {
    let ws = new WebSocket(url);

    let observable = Observable.create(
	(obs: Observer<MessageEvent>) => {
		ws.onmessage = obs.next.bind(obs);
		ws.onerror = obs.error.bind(obs);
		ws.onclose = obs.complete.bind(obs);
		return ws.close.bind(ws);
	});
    let observer = {
		next: (data: Object) => {
			if (ws.readyState === WebSocket.OPEN) {
				ws.send(JSON.stringify(data));
			}
		}
	}
	return Subject.create(observer, observable);
  }*/
  
}