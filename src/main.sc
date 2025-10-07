require: slotfilling/slotFilling.sc
    module = sys.zb-common
  
require: text/text.sc
    module = sys.zb-common

require: common.js
    module = sys.zb-common
    
require: scripts/functions.js

# Для игры Виселица
require: hangmanGameData.csv
    name = HangmanGameData
    var = $HangmanGameData

patterns:
    $Word = $entity<HangmanGameData> || converter = function ($parseTree) {
        var id = $parseTree.HangmanGameData[0].value;
        return $HangmanGameData[id].value;
        }
    $Letter = $regexp_i<\b[а-яёА-ЯЁ]\b>

theme: /

    state: EntryPoint
        q!: $regex</start>
        intent!: /Привет 
        script:
            $session = {}
            $client = {}
            $temp = {}
            $response = {}
        a: Слышишь, может в "Виселицу" разок сыгранём?
        go!: /EntryPoint/Согласен?
            
        state: Согласен?

            state: Да
                intent: /Согласие
                go!: /Hangman

            state: Нет
                intent: /Несогласие
                a: Ага, ломаешься, значит! Как надумаешь, пиши «Давай поиграем».
        
    state: Hangman
        intent!: /Давай поиграем
        a: Хорош! Кста, а ты правила-то помнишь? Могу напомнить, если надо.
        
        state: Rules
            intent: /Согласие/Надо
            a: Все просто: я загадываю слово, а ты пытаешься его отгадать. Можно угадывать по букве за ход, а можно все слово сразу. Как только слово назовешь - победа!
            a: Только наобум не называй: 6 раз ошибешься со словом или буквой и game over, это считается за проигрыш. Начнем?
            go!: /Hangman/Rules/Согласен?
            
            state: Согласен?

                state: Да
                    intent: /Согласие
                    go!: /Hangman/Start

                state: NotStart
                    intent: /Несогласие
                    a: Ну как хочешь. Передумаешь - зови, я всегда готов. Просто напиши «Давай поиграем».
            
        state: Start
            intent: /Несогласие/НеНадо
            a: Ну погнали тогда.
            script:
                $session.keys = Object.keys($HangmanGameData)
                $session.guess = 0
            go!:/Play

        state: LocalCatchAll
            event: noMatch
            random:
                a: Ты чего? Я спрашиваю, надо напомнить правила «Виселицы»?
                a: Не врубаюсь, так тебе повторить правила?
                
    state: Play
        intent!: /Новая игра
        script:
            if ($session.guess === 0) {
                
            $session.key = chooseRandKey($session.keys)
            $session.keys.splice($session.key, 1)
            
            $session.word = $HangmanGameData[$session.key].value.word
            $session.guess = $session.word.charAt(0).toUpperCase() + $session.word.slice(1)
            
            $session.numErrors = 0
            $session.usedLetters = []
            
            $session.hidden = displayWord($session.word, $session.usedLetters)
            $reactions.answer(selectRandomArg(["Хе, придумал! Вот твое слово: " + $session.hidden, "Загадал тебе слово! Смотри: " + $session.hidden]))
            
            } else { 
                
                if ($session.numErrors === 6) {
                    $reactions.answer("А все, попытки закончились! Слово было " + $session.guess)
                    $reactions.answer("Не расстраивайся только. Давай ещё раунд сыграем? Пиши «Новая игра».")
                    $session.guess = 0
                
                } else {
                    $reactions.answer(selectRandomArg(["Какой еще вариант хочешь проверить?", "Что теперь? Жду твоих догадок.", "Ходи, называй что-нибудь."]))
                    }}
                    
        state: PlayerSayLetter
            q: $Letter
            q: * [буква] $Letter
            script:
                var letter = $parseTree._Letter.toLowerCase()
                if ($session.usedLetters.indexOf(letter) === -1) {
                    
                    $session.usedLetters.push(letter)
                    
                    if ($session.word.indexOf(letter) !== -1) {
                        
                        if (isWordGuessed($session.word, $session.usedLetters)) {
                            
                            $reactions.answer("Еееее, победа!")
                            $reactions.answer($session.guess)
                            $reactions.answer("Го еще разик? Если хочешь, напиши мне «Новая игра».")
                            $session.guess = 0
                            
                        } else {
                            
                            $session.hidden = displayWord($session.word, $session.usedLetters)
                            $reactions.answer("О-о-откройте букву:" + $session.hidden)
                            $reactions.transition("/Play")
                            
                            }
                        
                    } else {
                        
                        $session.hidden = displayWord($session.word, $session.usedLetters)
                        $reactions.answer($session.hidden)
                        $session.numErrors = $session.numErrors + 1
                        $reactions.answer("Не-а, такой буквы нет. Потрачено " + $session.numErrors + "/6 попыток.")
                        $reactions.transition("/Play")
                        
                        }
                    
                } else {
                    
                    $session.hidden = displayWord($session.word, $session.usedLetters)
                    $reactions.answer("Эта буква уже была! Что-то другое называй.")
                    $reactions.answer($session.hidden)
                    $reactions.transition("/Play")
                    
                    }

                
    
    state: NoMatch || noContext = true
        event!: noMatch
        random:
            a: А по-русски можно? Я не понял.
            a: Чего?
            a: Сорян, не могу разобрать, что говоришь.
















    #state: reset
       # q!: reset
       # script:
        #    $session = {};
       #     $client = {};
       # go!: /Start