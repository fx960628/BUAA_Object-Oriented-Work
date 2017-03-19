package core;

/**
 * Created on 2017-03-13.
 * command schedule
 * this class is just for the project 2
 */
class Dispatcher implements ElevatorConstant {
    protected Elevator myElevator;
    protected RequestQueue requestQueue;
    Dispatcher(RequestQueue requestQueue){
        myElevator = new Elevator();
        this.requestQueue = requestQueue;
    }
    //this function is the most important task.
    void carryOutTheElevator(){
        int beforeRequestTime = 0;
        boolean isFirst = true;
        while(requestQueue.haveNext()){
            checkSameRequest(beforeRequestTime);//check later,reset beforeRequestTime
            SingleRequest request;
            if(requestQueue.haveNext())
                request= new SingleRequest(requestQueue.getRequestNext(),beforeRequestTime);
            else{
                myElevator.outPut();
                return;
            }
            if(!request.isLegalRequest()) //find the request is illegal
                continue;
            //deal the first not start at 0
            if(isFirst){
                if(request.getRequestTime()!=0){
                    Main.illegalMessage.add("The first Legal request is not (FR,1,UP,0),the program will exit.");
                    Main.outIllegalMessage();
                }else
                    isFirst = false;
            }
            myElevator.outPut();
            myElevator.setCompleteRequest(request);
            myElevator.setCompleteTime();
            myElevator.setMoveDire();
            beforeRequestTime = request.getRequestTime();
        }
        myElevator.outPut();
    }
    protected void checkSameRequest(int time){
        int loopStart = requestQueue.getIndexOfFetch();
        SingleRequest objRequest = myElevator.getCompleteRequest();
        SingleRequest targetRequest;
        double completeTime = myElevator.getCompleteTime();
        int beforeRequestTime = time;
        String request;

        if(objRequest==null)
            return;
        for(;loopStart<requestQueue.getSizeOfQueue();loopStart++){
            request = requestQueue.getRequestAt(loopStart);
            targetRequest = new SingleRequest(request,beforeRequestTime);
            if(targetRequest.isLegalRequest()){
                if(targetRequest.getRequestTime()>completeTime) //if true,not need loop again.exit the function
                    break;
                beforeRequestTime = targetRequest.getRequestTime();//for next judge.
            }else{
                requestQueue.delRequestAt(loopStart);
                loopStart--;
                continue;
            }
            if(objRequest.getRequestType()==2 && targetRequest.getRequestType()==2){ //--> FR
                if((objRequest.getMoveType()==1 && targetRequest.getMoveType()==1) ||
                        (objRequest.getMoveType()==2 && targetRequest.getMoveType()==2)){//-->UP || DOWN
                    if(objRequest.getTargetFloor()==targetRequest.getTargetFloor()){//same floor
                        Main.illegalMessage.add(request+" is redundant,the program will ignore this request.");
                        requestQueue.delRequestAt(loopStart);
                        loopStart--;
                    }
                }
            }else if(objRequest.getRequestType()==1 && targetRequest.getRequestType()==1){//--> ER
                if(objRequest.getTargetFloor()==targetRequest.getTargetFloor()){
                    Main.illegalMessage.add(request+" is redundant,the program will ignore this request.");
                    requestQueue.delRequestAt(loopStart);
                    loopStart--;
                }
            }
        }
    }
}