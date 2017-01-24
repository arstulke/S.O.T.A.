String.prototype.replaceAll = function(regex, replacements) {
    return this.split(regex).join(replacements);
}

function preLoad(arrayOfImages) {
		$(arrayOfImages).each(function(){
			$('<img/>')[0].src = this;
			// Alternatively you could use:
			// (new Image()).src = this;
		});
	}

	preLoad(["./img/v.png","./img/backsplash.png","./img/e.png","./img/error.png","./img/hashtag.png","./img/i.png","./img/k_.png","./img/ke.png","./img/ki.png","./img/kk.png","./img/kr.png","./img/kt.png","./img/kv.png","./img/n.png","./img/o.png","./img/slash.png","./img/space.png","./img/spike.png","./img/star.png","./img/stick.png","./img/t.png","./img/v.png","./img/x.png"]);


if (window.location.search.length > 0) {
    var size = window.location.search.substring(1);
    document.getElementById("output").style.fontSize = size;
    document.getElementById("right-cornor").style.fontSize = Math.round(size * 0.2778);
    document.getElementById("message").style.fontSize = Math.round(size * 0.2778);
}

$.ajax({
    url: "/maps",
    success: function(response) {
        for (var i = 0; i < response.length; i++) {
            var item = "<li onclick='startGame(\"" + response[i].id + "\")' style='cursor: pointer;'>" + response[i].name + "</li>";
            $("#map-list").html($("#map-list").html() + item);
        }
    }
})

function startGame(mapID) {
    $("#map-list").css("display", "none");
    $("#game-content").css("display", "block");

    var webSocket = new WebSocket('ws://' + window.location["hostname"] + '/game?title=' + mapID);
    var messages = [];
    webSocket.onmessage = function(message) {
        message = JSON.parse(message.data);
        if (message.cmd == "OUTPUT") {
            document.getElementById("right-cornor").innerHTML = "x: " + message.position.x + ", y:" + message.position.y;
            document.getElementById("output").innerHTML = message.msg;

            show(message.msg);

            (function() {
                var background = message.style.background;
                if (background.startsWith("#")) {
                    document.getElementById("output").style.backgroundImage = "";
                    if (document.getElementById("output").backgroundColor !== background) {
                        document.getElementById("output").backgroundColor = background;
                    }
                } else {
                    if (document.getElementById("output").style.backgroundImage !== background) {
                        document.getElementById("output").style.backgroundImage = "url('" + background + "')";
                    }
                    document.getElementById("output").backgroundColor = "";
                }
                document.getElementById("output").style.color = message.style.foreground;
            })();
            updateMessages();
        } else if (message.cmd == "PING-OUTPUT") {
            updateMessages();
        } else if (message.cmd == "CLEAR-MESSAGES") {
            messages = [];
            updateMessages();
        } else if (message.cmd == "MESSAGE") {
            messages[messages.length] = message.msg;
        }
    }

    function updateMessages() {
        //countdown ticks
        (function() {
            for (var i = 0; i < messages.length; i++) {
                messages[i].ticks = messages[i].ticks - 1;
                if (messages[i].ticks == 0) {
                    messages[i] = null;
                }
            }
        })();

        //remove expired messages
        (function() {
            newMessages = [];
            for (var i = 0; i < messages.length; i++) {
                if (messages[i] != null) {
                    newMessages[newMessages.length] = messages[i];
                }
            }
            messages = newMessages;
        })();

        //build output from messages
        var output = (function() {
            var out = "<li>";
            for (var i = 0; i < messages.length; i++) {
                out += messages[i].text.replaceAll("\n", "<br>") + "</li><li>";
            }
            return out.substring(0, out.length - 4);
        })();
        var messageOutputElement = document.getElementById("message");
        if (messageOutputElement.innerHTML !== output) {
            messageOutputElement.innerHTML = output;
        }
    }

    var keyManager = new KeyManager({
        "right": false,
        "left": false,
        "up": false,
        "down": false,
        "respawn": false
    });
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

    function KeyManager(keyCodes) {
        return {
            keys: keyCodes,
            changeKeyDown: function(key, name, event) {
                if (key == event.key && !this.keys[name]) {
                    this.keys[name] = true;
                    webSocket.send(name);
                }
            },
            changeKeyUp: function(key, name, event) {
                if (key == event.key && this.keys[name]) {
                    this.keys[name] = false;
                    webSocket.send(name);
                }
            }
        };
    }


    function show(str) {
        document.getElementById("output").innerHTML = "";
        var line = 0;
        for (var i = 0, len = str.length; i < len; i++) {
            if (str[i] == "/") {
                show_image("slash");
            } else if (str[i] == "\\") {
                show_image("backslash",i - ( line * 32),line);
            } else if (str[i] == ":") {
                show_image("double",i - ( line * 32),line);
            } else if (str[i] == "*") {
                show_image("star",i - ( line * 32),line);
            } else if (str[i] == "?") {
                show_image("questionmark",i - ( line * 32),line);
            } else if (str[i] == "\"") {
                show_image("syno",i - ( line * 32),line);
            } else if (str[i] == "<") {
                show_image("smaller",i - ( line * 32),line);
            } else if (str[i] == ">") {
                show_image("bigger",i - ( line * 32),line);
            } else if (str[i] == "|") {
                show_image("stick",i - ( line * 32),line);
            } else if (str[i] == " ") {
                show_image("space",i - ( line * 32),line);
            } else if (str[i] == "^") {
                show_image("spike",i - ( line * 32),line);
            } else if (str[i] == "#") {
                show_image("hashtag",i - ( line * 32),line);
            } else if (str[i] == "\n") {
                line += 1;
            } else {
                if (str[i] == str[i].toLowerCase()) {
                    show_image("k" + str[i]),i - ( line * 32),line;
                } else {
                    show_image(str[i].toLowerCase(),i - ( line * 32),line);
                }
            }
        }
    }

    function show_image(src,x,y) {
       /* if (src === "br") {
            document.getElementById("output").appendChild(document.createElement("br"));
        } else {
            var img = document.createElement("img");
            img.src = "./img/" + src + ".png";
            img.onerror = function() {
                this.src = "./img/error.png";
            }
            // This next line will just add it to the <body> tag
            document.getElementById("output").appendChild(img);
        }*/
        var c = document.getElementById("myCanvas");
        var ctx = c.getContext("2d");
        var img = document.getElementById("scream");
        ctx.drawImage(images[src],x,y);
    }
}