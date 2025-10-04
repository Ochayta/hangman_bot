require: slotfilling/slotFilling.sc
    module = sys.zb-common
  
require: text/text.sc
    module = sys.zb-common

require: common.js
    module = sys.zb-common

# Для игры Виселица
require: hangmanGameData.csv
    name = HangmanGameData
    var = $HangmanGameData

patterns:
    $Word = $entity<HangmanGameData> || converter = function ($parseTree) {
        var id = $parseTree.HangmanGameData[0].value;
        return $HangmanGameData[id].value;
        };

theme: /

    state: EntryPoint
        q!: $regex</start>
        intent!: /Привет 
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
        
        state: Hangman/Rules
            intent: /Согласие/Надо
            a: Все просто: я загадываю слово, а ты пытаешься его отгадать. Можно угадывать по букве за ход, а можно все слово сразу. Как только слово назовешь - победа!
            a: Только наобум не называй: 6 раз ошибешься со словом или буквой и game over, это считается за проигрыш. Начнем?
            go!: /Hangman/Rules/Согласен?
            
            state: Согласен?

                state: Да
                    intent: /Согласие
                    go!: /Hangman/Start

                state: Hangman/NotStart
                    intent: /Несогласие
                    a: Ну как хочешь. Передумаешь - зови, я всегда готов. Просто напиши «Давай поиграем».
            
        state: Hangman/Start
            intent: /Несогласие/НеНадо
            a: Ну погнали тогда.
            go!: /Hangman/Play
            
        state: Hangman/LocalCatchAll
            event: noMatch
            random:
                a: Ты чего? Я спрашиваю, надо напомнить правила «Виселицы»?
                a: Не врубаюсь, так тебе повторить правила?
                
        state: Hangman/Play
            a: успешный успех
        

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

