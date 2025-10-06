function chooseRandKey(keys) {
    return $jsapi.random(keys.length);
}

function isWordGuessed(word, usedLetters) {
    var uniqueLetters = new Set(word.split(''))
    
    for (var letter of uniqueLetters) {
        if (!usedLetters.includes(letter)) {
            return false;
        }
    }
    return true;
}