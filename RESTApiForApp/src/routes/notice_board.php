<?php
use \Psr\Http\Message\ServerRequestInterface as Request;
use \Psr\Http\Message\ResponseInterface as Response;




//Our Work

//Get all notices
$app->get('/api/notice_board',function(Request $request, Response $response){
 	$sql = "SELECT * FROM notice_board";

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