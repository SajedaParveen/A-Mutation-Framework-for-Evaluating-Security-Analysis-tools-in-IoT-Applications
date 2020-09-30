

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
    log.debug"Installed with settings:${settings}"
    
    initialize()
}

def updated(){
    
    def message="Hello"
    
    message="${weatherDevices}"
    
    sendPush(message)
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

