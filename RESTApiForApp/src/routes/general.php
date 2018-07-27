<?php
use \Psr\Http\Message\ServerRequestInterface as Request;
use \Psr\Http\Message\ResponseInterface as Response;



function insert_update_row($sql){
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
        return $responseData;

            }catch(PDOException $ex){
            	$responseData['error'] = true;
            	$responseData['message'] = $ex->getMessage();
                return $responseData;
    }  
}
function select_single_row($sql,$name){
    // Get DB 
    $responseData = array();
    try{
            $db = new db();
        // Connect
        $db = $db->connect();

        $stmt = $db->query($sql);
        $row = $stmt->fetch(PDO::FETCH_ASSOC);

        $db = null;
        $responseData['error'] = false;
        $responseData[$name] = $row;
        
    }catch(PDOException $ex){

        $responseData['error'] = true;
        $responseData['message'] = $ex->getMessage();
    }
return $responseData;
}
function select_multiple_rows($sql,$name){
    // Get DB 
    $responseData = array();
    try{
            $db = new db();
        // Connect
        $db = $db->connect();

        $stmt = $db->query($sql);
        $rows = $stmt->fetchAll(PDO::FETCH_ASSOC);

        $db = null;
        $responseData['error'] = false;
        $responseData[$name] = $rows;
        
    }catch(PDOException $ex){

        $responseData['error'] = true;
        $responseData['message'] = $ex->getMessage();
    }
return $responseData;
}
?>