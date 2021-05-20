String.prototype.replaceAll = function (regex, replacements) {
    return this.split(regex).join(replacements);
};

var images = {};

function preLoad(arrayOfImages, mapID) {
    var ready = arrayOfImages.length;
    console.log(arrayOfImages);
    $(arrayOfImages).each(function () {
        var key = this;
        var img = new Image();
        img.src = this;
        img.onload = function () {
            ready += -1;

            if (ready === 0) {
                startGame(mapID);
            }
        };
        img.onerror = function () {
            ready += -1;
            images[key] = null;
        };
        images[key] = img;
    });
}

$.ajax({
    url: "/maps",
    success: function (response) {
        for (var i = 0; i < response.length; i++) {
            var item = "<li onclick='initGame(\"" + response[i].id + "\")' style='cursor: pointer;'>" + response[i].name + "</li>";
            var mapList = $("#map-list");
            mapList.html(mapList.html() + item);

            $("#map-input-btn").click(function () {
                initGame($("#map-input").val());
            });
        }
    }
});

function initGame(mapID) {
    $.ajax({
        url: "/resources?map=" + mapID + "&mode=textures",
        success: function (resources) {
            preLoad(resources, mapID);
        }
    })
}

function startGame(mapID) {
    $("#map-selection").css("display", "none");
    $("#game-content").css("display", "block");

    resize(2);

    var webSocket = new WebSocket('ws://' + window.location["host"] + '/game?map=' + mapID);
    var messages = [];

    var canvas = document.getElementById("myCanvas");
    var canvasContext = canvas.getContext("2d");


    webSocket.onmessage = function (message) {
        message = JSON.parse(message.data);
        if (message["cmd"] === "OUTPUT") {
            document.getElementById("right-corner").innerHTML = "x: " + message.position.x + ", y:" + message.position.y;

            canvasContext.clearRect(0, 0, canvas.width, canvas.height);

            var background = message.style.background;
            if (background.startsWith("#")) {
                canvasContext.fillStyle = background;
                canvasContext.fillRect(0, 0, canvas.width, canvas.height);
            } else {
                var img = images[background];
                if (img !== null) {
                    canvasContext.drawImage(img, 0, 0);
                }
            }

            show(message["msg"], message["player_char"]);
            updateMessages();
        } else if (message["cmd"] === "PING-OUTPUT") {
            updateMessages();
        } else if (message["cmd"] === "CLEAR-MESSAGES") {
            messages = [];
            updateMessages();
        } else if (message["cmd"] === "MESSAGE") {
            messages[messages.length] = message["msg"];

        }
    };

    function updateMessages() {
        //countdown ticks
        (function () {
            for (var i = 0; i < messages.length; i++) {
                messages[i].ticks = messages[i].ticks - 1;
                if (messages[i].ticks === 0) {
                    messages[i] = null;
                }
            }
        })();

        //remove expired messages
        (function () {
            var newMessages = [];
            for (var i = 0; i < messages.length; i++) {
                if (messages[i] !== null) {
                    newMessages[newMessages.length] = messages[i];
                }
            }
            messages = newMessages;
        })();

        //build output from messages
        var output = (function () {
            var out = "<li>";
            for (var i = 0; i < messages.length; i++) {
                out += messages[i].text.replaceAll("\n", "<br>") + "</li><li>";
            }
            return out.substring(0, out.length - 4);
        })();
        var messageOutputElement = document.getElementById("message");
        if (messageOutputElement.innerHTML !== output) {
            messageOutputElement.innerHTML = output;

            var messageContainer = document.getElementById("message-container");
            if (messages.length === 0) {
                messageContainer.style.display = "none";
            } else {
                messageContainer.style.display = "block";
            }
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
    window.onkeydown = function (e) {
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
    };

    window.onkeyup = function (e) {
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
    };

    function KeyManager(keyCodes) {
        return {
            keys: keyCodes,
            changeKeyDown: function (key, name, event) {
                if (key === event.key && !this.keys[name]) {
                    this.keys[name] = true;
                    webSocket.send(name);
                }
            },
            changeKeyUp: function (key, name, event) {
                if (key === event.key && this.keys[name]) {
                    this.keys[name] = false;
                    webSocket.send(name);
                }
            }
        };
    }


    function show(str, playerChar) {
        str = str.split("\n");
        for (var y = 0; y < str.length; y++) {
            var line = str[y];
            for (var x = 0; x < line.length; x++) {
                showImageByChar(line[x], x, y);
            }
        }

        showImageByChar(playerChar, 15, 4);
    }

    function showImageByChar(char, x, y) {
        var replacements = {};
        replacements["/"] = "slash";
        replacements["."] = "dot";
        replacements[","] = "comma";
        replacements["\\"] = "backslash";
        replacements[":"] = "double";
        replacements["*"] = "star";
        replacements["?"] = "questionmark";
        replacements["\""] = "syno";
        replacements["<"] = "smaller";
        replacements[">"] = "bigger";
        replacements["|"] = "stick";
        replacements[" "] = "space";
        replacements["^"] = "spike";
        replacements["#"] = "hashtag";
        replacements["+"] = "plus";
        replacements["-"] = "minus";

        if (replacements[char] !== undefined) {
            show_image(replacements[char], x, y);
        } else if (char === char.toLowerCase()) {
            show_image("k" + char, x, y);
        } else {
            show_image(char.toLowerCase(), x, y);
        }
    }

    function show_image(src, x, y) {
        try {
            canvasContext.drawImage(images["/textures?map=" + mapID + "&name=" + src], x * 16, y * 32);
        } catch (e) {
            canvasContext.drawImage(images["/error.png"], x * 16, y * 32);
            console.error("src: " + src + ", x:" + x + ", y:" + y);
        }
    }
}


function resize(factor) {
    var canvas = document.getElementById("myCanvas");
    var canvasContext = canvas.getContext("2d");

    canvas.width = canvas.width * factor;
    canvas.height = canvas.height * factor;
    canvasContext.scale(factor, factor);
}
