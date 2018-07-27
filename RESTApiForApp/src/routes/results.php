<?php
use \Psr\Http\Message\ServerRequestInterface as Request;
use \Psr\Http\Message\ResponseInterface as Response;


require_once "../src/routes/general.php";
$app->get('/api/updates',function(Request $request, Response $response){

$sql = "select * from date_sheet";
$sql1 = "select * from time_table";
$sql2= "select * from attendence_report";
 	$responseData = array();
 	try{
 		// Get DB 
 			$db = new db();
 		// Connect
 		$db = $db->connect();

		$stmt = $db->query($sql);
 		$datesheets = $stmt->fetchAll(PDO::FETCH_OBJ);

 		$stmt = $db->query($sql1);
 		$time_table = $stmt->fetchAll(PDO::FETCH_OBJ);

 		$stmt = $db->query($sql2);
 		$attendance_report = $stmt->fetchAll(PDO::FETCH_OBJ);

 		$db = null;


 		
            $responseData['error'] = "false";
        
            $responseData['message'] = null;

            $responseData['datesheets'] = $datesheets;

            $responseData['time_table'] = $time_table;

            $responseData['attendance_report'] = $attendance_report;


        
 		
 			}catch(PDOException $ex){
 		    $responseData['error'] = "true";
            $responseData['message'] = $ex->getMessage();
 	}
        $response->getBody()->write(json_encode($responseData));

});

?>
