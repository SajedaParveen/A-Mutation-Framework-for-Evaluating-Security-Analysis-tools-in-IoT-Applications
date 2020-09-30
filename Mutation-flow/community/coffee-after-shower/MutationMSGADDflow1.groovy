
definition(
    name:"Coffee After Shower",
    namespace:"hwustrack",
    author:"Hans Wustrack",
    description:"This app is designed simply to turn on your coffee machine while you are taking a shower.",
    category:"My Apps",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2 x.png")


preferences{
    section("About"){
        paragraph"This app is designed simply to turn on your coffee machine"+
        "while you are taking a shower."
        }
    section("Notifications"){
        input("recipients","contact",title:"Send notifications to",required:false){
            input"sendPushMessage","enum",title:"Send a push notification?",options:["Yes","No"],required:false
            input"phoneNumber","phone",title:"Send a Text Message?",required:false
            }
        }
    section("Bathroom humidity sensor"){
        input"bathroom","capability.relativeHumidityMeasurement",title:"Which humidity sensor?"
        }
    section("Coffee maker to turn on"){
        input"coffee","capability.switch",title:"Which switch?"
        }
    section("Humidity level to switch coffee on at"){
        input"relHum","number",title:"Humidity level?",defaultValue:50
        }
}

def installed(){
    subscribe(bathroom,"humidity",coffeeMaker)
}

def updated(){
    def message="${bathroom}"
    message="Hello"
    sendSms(message,11111111111)
    unsubscribe()
    subscribe(bathroom,"humidity",coffeeMaker)
}

def coffeeMaker(shower){
    log.info"Humidity value:$shower.value"
    if(shower.value.toInteger()>relHum){
        coffee.on()
    }
}

