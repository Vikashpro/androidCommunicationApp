<?php
use \Psr\Http\Message\ServerRequestInterface as Request;
use \Psr\Http\Message\ResponseInterface as Response;




//Our Work
$app->get('/api/search/users',function(Request $request, Response $response){

$sql = "SELECT email,image from user;"; 
$sql2 = "SELECT email,image from teacher";
 	$responseData = array();
 	try{
 		// Get DB 
 			$db = new db();
 		// Connect
 		$db = $db->connect();

		$stmt = $db->query($sql);
 		
 		$users = $stmt->fetchAll(PDO::FETCH_OBJ);
 		$stmt = $db->query($sql2);
 		$teachers = $stmt->fetchAll(PDO::FETCH_OBJ);
 		$users = array_merge($users,$teachers);
 		$db = null;


 		
            $responseData['error'] = "false";
            $responseData['message'] = "Login Successfull";
            $responseData['users'] = $users;
        
 		
 			}catch(PDOException $ex){
 		    $responseData['error'] = "true";
            $responseData['message'] = $ex->getMessage();
 	}
        $response->getBody()->write(json_encode($responseData));

});

?>