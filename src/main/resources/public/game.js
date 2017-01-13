String.prototype.replaceAll = function(regex, replacements) {
		return this.split(regex).join(replacements);
	}


	if(window.location.search.length > 0) {
        var size = window.location.search.substring(1);
		document.getElementById("output").style.fontSize = size;
		document.getElementById("right-cornor").style.fontSize = Math.round(size * 0.2778);
		document.getElementById("message").style.fontSize = Math.round(size * 0.2778);
	}

	var webSocket = new WebSocket('ws://' + window.location["hostname"] + '/game');
	var messages = [];
	webSocket.onmessage = function(message) {
		message = JSON.parse(message.data);
		if(message.cmd == "OUTPUT") {
			document.getElementById("right-cornor").innerHTML = "x: " + message.position.x + ", y:" + message.position.y;
			document.getElementById("output").innerHTML = message.msg;

            document.body.background = message.style.background;
            document.getElementById("output").style.color = message.style.foreground;
			updateMessages();
		} else if(message.cmd == "PING-OUTPUT") {
		    updateMessages();
		} else if(message.cmd == "CLEAR-MESSAGES") {
		    messages = [];
		    updateMessages();
		} else if(message.cmd == "MESSAGE") {
            messages[messages.length] = message.msg;
		}
	}

	function updateMessages() {
	    //countdown ticks
        (function() {
            for(var i = 0; i < messages.length; i++) {
                messages[i].ticks = messages[i].ticks - 1;
                if(messages[i].ticks == 0) {
                    messages[i] = null;
                }
            }
        })();

        //remove expired messages
	    (function() {
            newMessages = [];
            for(var i = 0; i < messages.length; i++) {
                if(messages[i] != null) {
                    newMessages[newMessages.length] = messages[i];
                }
            }
            messages = newMessages;
        })();

        //build output from messages
	    var output = (function() {
	        var out = "<li>";
	        for(var i = 0; i < messages.length; i++) {
	            out += messages[i].text.replaceAll("\n", "<br>") + "</li><li>";
	        }
	        return out.substring(0, out.length - 4);
	    })();
	    var messageOutputElement = document.getElementById("message");
	    if(messageOutputElement.innerHTML !== output) {
	        messageOutputElement.innerHTML = output;
        }
	}

	var keyManager = new KeyManager({"right":false, "left":false, "up":false, "down":false, "respawn": false});
	var up = false;
	window.onkeydown = function(e) {
		keyManager.changeKeyDown("w", "up", e);
		keyManager.changeKeyDown("ArrowUp", "up", e);
		keyManager.changeKeyDown(" ", "up", e);

		keyManager.changeKeyDown("d", "right", e);
		keyManager.changeKeyDown("ArrowRight", "right", e);

		keyManager.changeKeyDown("a", "left", e);
		keyManager.changeKeyDown("ArrowLeft", "left", e);

		keyManager.changeKeyDown("s", "down", e);
		keyManager.changeKeyDown("ArrowDown", "down", e);

		keyManager.changeKeyDown("r", "respawn", e);
	}
	window.onkeyup = function(e) {
		keyManager.changeKeyUp("w", "up", e);
		keyManager.changeKeyUp("ArrowUp", "up", e);
		keyManager.changeKeyUp(" ", "up", e);

		keyManager.changeKeyUp("d", "right", e);
		keyManager.changeKeyUp("ArrowRight", "right", e);

		keyManager.changeKeyUp("a", "left", e);
		keyManager.changeKeyUp("ArrowLeft", "left", e);

		keyManager.changeKeyUp("s", "down", e);
		keyManager.changeKeyUp("ArrowDown", "down", e);

		keyManager.changeKeyUp("r", "respawn", e);
	}

	function KeyManager(keyCodes){
		return {
			keys: keyCodes,
			changeKeyDown: function(key, name, event){
				if(key == event.key && !this.keys[name]){
					this.keys[name] = true;
					sendToServer(name);
				}
			},
			changeKeyUp: function(key, name, event) {
				if(key == event.key && this.keys[name]) {
					this.keys[name] = false;
					sendToServer(name);
				}
			}
		};
	}

	function sendToServer(msg) {
		webSocket.send(msg);
	}