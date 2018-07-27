<?php
use \Psr\Http\Message\ServerRequestInterface as Request;
use \Psr\Http\Message\ResponseInterface as Response;




//Our Work

//Get all notices
$app->get('/api/not',function(Request $request, Response $response){
 	$sql = "SELECT * FROM notice";

 	try{
 		// Get DB Object
 		$db = new db();
 		// Connect
 		$db = $db->connect();

 		//FETCHING DATA FROM THE OBJECT
 		$stmt = $db->query($sql);
 		$notices = $stmt->fetchAll(PDO::FETCH_OBJ);
 		$db = null;
 		return json_encode($notices);

 	}catch(PDOException $ex){
 		return '{"error": {"text": '.$ex->getMessage().'}';
 	}
});
//Get a single notice
$app->get('/api/notice/{notice_id}',function(Request $request, Response $response){
 	$id = $request->getAttribute('notice_id');
 	$sql = "SELECT * FROM notice WHERE notice_id = $id";

 	try{
 		// Get DB Object

 		$db = new db();
 		// Connect
 		$db = $db->connect();

 		//FETCHING DATA FROM THE OBJECT
 		$stmt = $db->query($sql);
 		$notice = $stmt->fetchAll(PDO::FETCH_OBJ);
 		$db = null;
 		echo json_encode($notice);

 	}catch(PDOException $ex){
 		echo '{"error": {"text": '.$ex->getMessage().'}';
 	}
});

//Add Notice

//Update Notice
$app->put('/api/notice/update/{id}',function(Request $request, Response $response){
 	$id = $request->getAttribute('id');
 	$user = $request->getParam('user');
 	$subject = $request->getParam('subject');
 	$description = $request->getParam('description');
 
 	
 	$sql = "UPDATE notice SET 
 	    		user = :user,
 	    		subject = :subject,
 	    		Description = :description 
 	    	WHERE notice_id = $id";


 	try{
 		// Get DB Object
 		$db = new db();
 		// Connect
 		$db = $db->connect();

 		$stmt = $db->prepare($sql);
 		$stmt->bindParam(':user',$user);	
 		$stmt->bindParam(':subject',$subject);
 		$stmt->bindParam(':description',$description);

 		$stmt->execute();
 		$db = null;
 		echo '{"note":{"text":"Notification Updated"}';
 			}catch(PDOException $ex){
 		echo '{"error": {"text": '.$ex->getMessage().'}';
 	}
});
//Delete a single notification
$app->delete('/api/notice/delete/{id}',function(Request $request, Response $response){
 	$id = $request->getAttribute('id');
 	$sql = "DELETE FROM notice WHERE notice_id = $id";

 	try{
 		// Get DB Object

 		$db = new db();
 		// Connect
 		$db = $db->connect();

 		//DELETING NOTIFICATION
 		$stmt = $db->prepare($sql);
 		$stmt->execute();
 		$db = null;
 		echo '{"note":{"text":"Notification Deleted"}';
 		

 	}catch(PDOException $ex){
 		echo '{"error": {"text": '.$ex->getMessage().'}';
 	}
});
?>