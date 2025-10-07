function chooseRandKey(keys) {
    return $jsapi.random(keys.length);
}

function isWordGuessed(word, usedLetters) {
    var cleanWord = word.toLowerCase().replace(/\s/g, '');
    for (var i = 0; i < cleanWord.length; i++) {
        var letter = cleanWord[i];
        if (usedLetters.indexOf(letter) === -1) {
            return false;
        }
    }
    return true;
}
