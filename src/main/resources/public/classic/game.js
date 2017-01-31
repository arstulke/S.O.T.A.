String.prototype.replaceAll = function(regex, replacements) {
    return this.split(regex).join(replacements);
}


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

            $("#map-input-btn").click(function(event) {
                initGame($("#map-input").val());
            });
        }
    }
})

function startGame(mapID) {
    $("#map-list").css("display", "none");
    $("#game-content").css("display", "block");

    var webSocket = new WebSocket('ws://' + window.location["hostname"] + '/game?map=' + mapID);
    var messages = [];


    var actualBackground = null;
    webSocket.onmessage = function(message) {
        message = JSON.parse(message.data);
        if (message.cmd == "OUTPUT") {
            document.getElementById("right-cornor").innerHTML = "x: " + message.position.x + ", y:" + message.position.y;
            show(message.msg, message.player_char);
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


    function show(str, playerChar) {
    document.getElementById("output").innerHTML = str;
    document.getElementById("output").innerHTML = document.getElementById("output").innerHTML.substr(0, 143) + playerChar + document.getElementById("output").innerHTML.substr(143 + 1);
    }
}

