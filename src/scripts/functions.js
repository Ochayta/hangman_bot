function chooseRandKey(keys) {
    return $jsapi.random(keys.length);
}

function isWordGuessed(word, usedLetters) {
    for (var i = 0; i < word.length; i++) {
        var letter = word[i];
        if (usedLetters.indexOf(letter) === -1) {
            return false;
        }
    }
    return true;
}

function displayWord(word, usedLetters) {
    var result = "";
    for (var i = 0; i < word.length; i++) {
        var letter = word[i];
        if (usedLetters.indexOf(letter) !== -1) {
            result += letter.toUpperCase() + " ";
        } else {
            result += "_ ";
        }
    }
    return result.trim();
}