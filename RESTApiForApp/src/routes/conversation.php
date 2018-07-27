<?php
use \Psr\Http\Message\ServerRequestInterface as Request;
use \Psr\Http\Message\ResponseInterface as Response;


require_once "../src/routes/general.php";



$app->post('/api/add/conversation',function(Request $request, Response $response){
    
    $flag = true;

     $sender_email = $request->getParam('sender_email');
     $participant = $request->getParam('receiver');
    $subject = $request->getParam('subject');
    $message = $request->getParam('message');
    
    $sql = "INSERT INTO conversation (subject) VALUES ('$subject');";
    

    
$responseData = array();

    try{
        // Get DB 
            $db = new db();
        // Connect
        $db = $db->connect();
        $stmt = $db->query($sql);
        $c_id = $db->lastInsertId();

        $db = null;

        if(add_participant($sender_email,$c_id)){
        if(add_participant($participant,$c_id)){
       
        
        if(add_message($message,$sender_email,$c_id)){
              $responseData['error'] = false;
            
        }else{
            $responseData['message'] = add_message($message,$id,$c_id);
            $responseData['error'] = true;
        
        }
             
      }else{
            $responseData['message'] = add_participant($participant,$c_id);
            $responseData['error'] = true;
        }
    
    }else{
            $responseData['message'] = add_participant($sender_email,$c_id);
            
            $responseData['error'] = true;
         }
    
        

        
            }catch(PDOException $ex){
        $responseData['error'] = true;
            $responseData['message'] = $ex->getMessage();
    }
    $response->getBody()->write(json_encode($responseData));
});
$app->post('/api/add/participant',function(Request $request, Response $response){
    $added_by = $request->getParam('added_by');
    $participant = $request->getParam('participant_email');
    $c_id = $request->getParam('c_id');
    
    $responseData = add_participant($participant,$c_id);      
        if(!$responseData['error']){
              
            $responseData['message'] = "$participant added!";
        }
         $response->getBody()->write(json_encode($responseData));
});

function add_participant($email,$c_id){
    $sql = "INSERT INTO participant (p_email,c_id) VALUES ('$email',$c_id)";
    return insert_update_row($sql);
}



$app->post('/api/conversations',function(Request $request, Response $response){
 $p_email = $request->getParam('email');

    $responseData = array();
    $sql = "SELECT conversation.c_id, conversation.subject,conversation.last_update, participant.unReadCounts,IF(participant.isImportant, 'true', 'false') isImportant FROM conversation JOIN participant ON conversation.c_id = participant.c_id WHERE participant.p_email = '$p_email' and participant.enabled = 1 ORDER BY conversation.last_update DESC ";

    $responseData = select_multiple_rows($sql,'conversation');
    
    $convos = array();
        if(!$responseData['error']){
        foreach($responseData['conversation'] as $i){
                $c_id = $i['c_id'];
                $ret =select_participants($c_id,$p_email);
                if(!$ret['error']){
                    $str = "";
                    foreach($ret['participants'] as $j)
                    {
                        $str .= $j['name']. ",";
                    }
                    $con = $i;
                    $con['name'] = substr($str,0, -1);
                    $con['image'] = "";
                    array_push($convos,$con);
              }else{
                    $responseData = $ret;
                    break;
                }
        }
        $responseData['conversation'] = $convos;

    }
   
      
        
       $response->getBody()->write(json_encode($responseData));


});
$app->post('/api/messages',function(Request $request, Response $response){
 
 $c_id = $request->getParam('c_id');
    $responseData = array();
    $sql = "SELECT user.name,participant.p_email,user.image , messages.m_id,messages.message,messages.timestamp FROM messages JOIN participant ON messages.p_id = participant.p_id JOIN user ON user.email = participant.p_email where messages.c_id = '$c_id' ORDER BY messages.m_id ASC";
   
    $responseData = select_multiple_rows($sql,'messages');
    
    
      
        
       $response->getBody()->write(json_encode($responseData));


});
function setFlag($sql){

$responseData = array();
       
    try{

        // Get DB Object
        $db = new db();
        // Connect
        $db = $db->connect();

        $stmt = $db->prepare($sql);
        
    
        $stmt->execute();
        $db = null;
     $responseData['error'] = false;
            
            }catch(PDOException $ex){
        $responseData['error'] = true;
            $responseData['message'] = $ex->getMessage();
      
}
return $responseData;

}

$app->post('/api/conversation/isImportant', function(Request $request, Response $response){
    
    $email = $request->getParam('email');
    $c_id = $request->getParam('c_id');
    $flag = $request->getParam('flag');
    $sql = "UPDATE participant SET isImportant = $flag WHERE c_id = $c_id and p_email = '$email'";
    $responseData = setFlag($sql);

    $response->getBody()->write(json_encode($responseData));

});
$app->post('/api/conversation/unReadCounts', function(Request $request, Response $response){
    
    $email = $request->getParam('email');
    $c_id = $request->getParam('c_id');
    $unReadCounts = $request->getParam('unReadCounts');
    $sql = "UPDATE participant SET unReadCounts = $unReadCounts WHERE c_id = $c_id and p_email = '$email'";
    
    $responseData = setFlag($sql);
    $response->getBody()->write(json_encode($responseData));
    

});
function select_participants($c_id,$p_email){
    // $sql = "SELECT participant.p_id,participant.c_id,user.name, user.image FROM user JOIN participant ON user.email = participant.p_email WHERE participant.c_id = $c_id and participant.p_email != '$p_email'"; 
       $sql = "SELECT participant.c_id,user.name FROM user JOIN participant ON user.email = participant.p_email WHERE participant.c_id = $c_id and participant.p_email != '$p_email'"; 
     
                return select_multiple_rows($sql, 'participants');

}


$app->post('/api/add/message',function(Request $request, Response $response){
 $sender_email = $request->getParam('sender_email');
$message = $request->getParam('message');
     $c_id = $request->getParam('c_id');
       
        $responseData = add_message($message,$sender_email,$c_id);
        if(!$responseData['error']){
                $sql1 = "UPDATE conversation SET last_update = current_timestamp where c_id = $c_id";
                insert_update_row($sql1);
                $timestamp =  date('Y-m-d G:i:s');

        require_once 'gcm.php';
        require_once 'push.php';
        $gcm = new GCM();
        $push = new Push();
 
        // get the user using userid
        $sql = "select id, name,email,image,gcm_registration_id from user where email = '$sender_email'";
            $ret = select_single_row($sql,'user');
            if(!$ret['error']){
                $user = $ret['user'];

        $data = array();
        $data['sender_email'] = $sender_email;
        $data['c_id'] = $c_id;
        $data['message'] = $message;
        $data['image_url'] = $user['image'];
        $data['name'] = $user['name'];
        $data['timestamp'] =$timestamp;
    
        $push->setTitle("Google Cloud Messaging");
        $push->setIsBackground(FALSE);
        $push->setFlag(PUSH_FLAG_USER);
        $push->setData($data);
        
 
        // sending push message to single user
        $responseData['results'] = $gcm->send($user['gcm_registration_id'], $push->getPush());

        
        }
    }

        $response->getBody()->write(json_encode($responseData));


});
function add_message($message,$sender_email,$c_id){
        $sql = "SELECT p_id from participant where p_email = '$sender_email' && c_id = $c_id";

        $ret = select_single_row($sql,'p_id');
     
     if(!$ret['error']){  
      $p_ids= $ret['p_id'];
       $p_id = $p_ids['p_id'];
    $sql = "INSERT INTO messages (message,p_id,c_id) VALUES ('$message',$p_id ,$c_id)";
    
    return insert_update_row($sql);
    }else{
        return $ret; 
    }
    return $ret;

}


?>