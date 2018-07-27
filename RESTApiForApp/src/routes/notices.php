<?php
use \Psr\Http\Message\ServerRequestInterface as Request;
use \Psr\Http\Message\ResponseInterface as Response;




//Our Work

//Get all notices




$app->get('/api/notification/notices',function(Request $request, Response $response){

$sql = "SELECT notice.notice_id, notice.subject,notice.description,notice.date,notice.sender,IF(notice.isRead, 'true', 'false') isRead,IF(notice.isImportant, 'true', 'false') isImportant, user.name,user.image FROM notice
INNER JOIN user
ON user.email = notice.sender_name";
 	$responseData = array();
 	try{
 		// Get DB 
 			$db = new db();
 		// Connect
 		$db = $db->connect();

		$stmt = $db->query($sql);
 		$notices = $stmt->fetchAll(PDO::FETCH_OBJ);

 		$db = null;


 		
            $responseData['error'] = "false";
        
            $responseData['notices'] = $notices;
        
 		
 			}catch(PDOException $ex){
 		    $responseData['error'] = "true";
            $responseData['message'] = $ex->getMessage();
 	}
        $response->getBody()->write(json_encode($responseData));

});


$app->post('/api/notice/add',function(Request $request, Response $response){
 	
 	$receiver = $request->getParam('receiver');
 	$subject = $request->getParam('subject');
 	$description = $request->getParam('description');
 	$sender_name = $request->getParam('sender_email');
 	
 	$sql = "INSERT INTO notice (receiver,subject,description,sender_name,dep_id) VALUES (:receiver,:subject,:description,:sender_name,'1')";
$responseData = array();

 	try{
 		// Get DB Object
 		$db = new db();
 		// Connect
 		$db = $db->connect();

 		$stmt = $db->prepare($sql);
 		$stmt->bindParam(':receiver',$receiver);
 		$stmt->bindParam(':subject',$subject);
 		$stmt->bindParam(':description',$description);
		$stmt->bindParam(':sender_name',$sender_name);
		
 		$stmt->execute();
 		$db = null;
 		
            $responseData['error'] = false;
            $responseData['message'] = "notice sent successfully";
 			}catch(PDOException $ex){
 		$responseData['error'] = true;
            $responseData['message'] = $ex->getMessage();
 	}
 	$response->getBody()->write(json_encode($responseData));
});
$app->post('/api/notice/isImportant', function(Request $request, Response $response){
    
    $email = $request->getParam('email');
    $notice_id = $request->getParam('notice_id');
    $flag = $request->getParam('flag');
    $responseData = setIsFlag($notice_id,$flag,$email,'isImportant');

    $response->getBody()->write(json_encode($responseData));

});
$app->post('/api/notice/isRead', function(Request $request, Response $response){
    
    $email = $request->getParam('email');
    $notice_id = $request->getParam('notice_id');
    
    $responseData = setIsFlag($notice_id,true,$email,'isRead');
    $response->getBody()->write(json_encode($responseData));
    

});
function setIsFlag($notice_id,$flag,$email,$column){

$responseData = array();
       $sql = "UPDATE notice SET $column = $flag WHERE notice_id = $notice_id ";
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

?>
