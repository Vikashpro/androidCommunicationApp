<?php
use \Psr\Http\Message\ServerRequestInterface as Request;
use \Psr\Http\Message\ResponseInterface as Response;

require_once "../src/routes/general.php";

$app->get('/api/notice_board/public_notices',function(Request $request, Response $response){

$sql = "SELECT notice_board.id, user.name,user.image,notice_board.feed,notice_board.notice,notice_board.timestamp,notice_board.link From notice_board JOIN user ON notice_board.sender_id = user.id ORDER BY notice_board.timestamp DESC";

 	$responseData = array();
 	try{
 		// Get DB 
 			$db = new db();
 		// Connect
 		$db = $db->connect();

		$stmt = $db->query($sql);
 		$public_notices = $stmt->fetchAll(PDO::FETCH_OBJ);

 		$db = null;


 		
            $responseData['error'] = "false";
        	$responseData['message'] = null;
            $responseData['public_notices'] = $public_notices;
        
 		
 			}catch(PDOException $ex){
 		    $responseData['error'] = "true";
            $responseData['message'] = $ex->getMessage();
 	}
        $response->getBody()->write(json_encode($responseData));

});


?>