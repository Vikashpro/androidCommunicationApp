<?php
use \Psr\Http\Message\ServerRequestInterface as Request;
use \Psr\Http\Message\ResponseInterface as Response;

require_once "../src/routes/general.php";


$app->post('/api/user/registration',function(Request $request, Response $response){
     $pass = md5($request->getParam('pass'));
     $name = $request->getParam('name');
     $email = $request->getParam('email'); 
     $cms_id = $request->getParam('cms_id');
     $dep_id = $request->getParam('dep_id');
     $semester = $request->getParam('semester');


    $sql = "INSERT INTO user (pass,name,email,cms_id,dep_id,semester) VALUES ('$pass','$name','$email','$cms_id','$dep_id','$semester')";
     
   $responseData = array();
    try{
        // Get DB 
            $db = new db();
        // Connect
        $db = $db->connect();

        $stmt = $db->query($sql);
        //$user = $stmt->fetchAll(PDO::FETCH_OBJ);
        $db = null;

        if ($stmt != null) {
            $responseData['error'] = "false";
            $responseData['message'] = "Login Successfull";
            $responseData['user'] = $stmt;
        } else {
            $responseData['error'] = "true";
            $responseData['message'] = 'Invalid email or password';
        }
 
        
            }catch(PDOException $ex){
            $responseData['error'] = "true";
            $responseData['message'] = $ex->getMessage();
    }
        $response->getBody()->write(json_encode($responseData));

});





$app->post('/api/user/login',function(Request $request, Response $response){

 	$pass = md5($request->getParam('pass'));
 	$email = $request->getParam('email');
 	$sql = "SELECT * FROM user where email='$email' and pass='$pass'";

 	$responseData = array();
 	try{
 		// Get DB 
 			$db = new db();
 		// Connect
 		$db = $db->connect();

		$stmt = $db->query($sql);
 		$user = $stmt->fetchAll(PDO::FETCH_OBJ);
 		$db = null;


 		if ($user != null) {
            $responseData['error'] = "false";
            $responseData['message'] = "Login Successfull";
            $responseData['user'] = $user;
        } else {
            $responseData['error'] = "true";
            $responseData['message'] = 'Invalid email or password';
        }
 
 		
 			}catch(PDOException $ex){
 		    $responseData['error'] = "true";
            $responseData['message'] = $ex->getMessage();
 	}
        $response->getBody()->write(json_encode($responseData));

});
$app->post('/api/user/update-pass',function(Request $request, Response $response){

    $email = $request->getParam('email');
    //$current_pass = $request->getParam('current_pass');
    $new_pass = md5($request->getParam('new_pass'));
    
    $sql = "UPDATE user SET 
                pass = :pass
                
            WHERE email = '$email'";
    try{

        // Get DB Object
        $db = new db();
        // Connect
        $db = $db->connect();

        $stmt = $db->prepare($sql);
        $stmt->bindParam(':pass',$new_pass);
    
        $stmt->execute();
        $db = null;
        echo '{"note":{"text":"Password Updated"}';
        
        
            }catch(PDOException $ex){
        echo '{"error": {"text": '.$ex->getMessage().'}';
    }
});


$app->post('/api/update/profile_pic',function(Request $request, Response $response){
 
    
    $email = $request->getParam('email');
 
    $file_tmp = $_FILES["file"]["tmp_name"];
    $file_name = $_FILES["file"]["name"];
    $file_type = $_FILES["file"]["type"];
    $new_image_name = uniqid("IMG_",false);

    if($file_type == 'image/jpeg'){
      $ext = explode(".",$_FILES['file']['name']);  
    $final_name = $new_image_name.".".$ext[1];

    }else{
        $final_name = $new_image_name;
    }
    $file_path = "images/".$final_name;

$responseData = array(); 

if(move_uploaded_file($file_tmp,$file_path)) {
        $ret = updateImage($email,$final_name);
        if($ret){
    
         $responseData['error'] = "false";
         $responseData['message'] = $ret;//"file successfully uploaded";
         

            $responseData['image_url'] = $file_path;
     
        }else{
            $responseData['error'] = "true";
            $responseData['message'] = $ret;
        }
}else{
     $responseData['error'] = "true";
    
} 
echo json_encode($responseData, JSON_PRETTY_PRINT);

});




$app->delete('/api/user/delete/{cms_id}',function(Request $request, Response $response){
    $cms_id = $request->getAttribute('cms_id');
    $sql = "DELETE FROM user WHERE cms_id = $cms_id";

    try{
        // Get DB Object

        $db = new db();
        // Connect
        $db = $db->connect();

        //DELETING NOTIFICATION
        $stmt = $db->prepare($sql);
        $stmt->execute();
        $db = null;
        echo '{"note":{"text":"User Deleted"}';
        

    }catch(PDOException $ex){
        echo '{"error": {"text": '.$ex->getMessage().'}';
    }
});

function updateImage($email,$file){

       $sql = "UPDATE user SET image = '$file' WHERE email = '$email'";
    try{
        // Get DB Object
        $db = new db();
        // Connect
        $db = $db->connect();
        $stmt = $db->prepare($sql);
        $stmt->execute();
        $db = null;
        return "$sql"; //"true";
            }catch(PDOException $ex){
        return $ex->getMessage();
    }  
}

   // updating user GCM registration ID
 function updateGcmID($email, $gcm_registration_id) {
        $response = array();
        $stmt->bind_param("ss", $gcm_registration_id, $email);
 
        if ($stmt->execute()) {
            // User successfully updated
            $response["error"] = false;
            $response["message"] = 'GCM registration ID updated successfully';
        } else {
            // Failed to update user
            $response["error"] = true;
            $response["message"] = "Failed to update GCM registration ID";
            $stmt->error;
        }
        $stmt->close();
 
        return $response;
    }
    /* * *
 * Updating user
 *  we use this url to update user's gcm registration id
 */



$app->post('/api/update_gcm',function(Request $request, Response $response){
    $email = $request->getParam('email');
    $gcm_registration_id = $request->getParam('token');
 
   // verifyRequiredParams(array('gcm_registration_id'));
 
    $sql = "UPDATE user SET gcm_registration_id = '$gcm_registration_id' WHERE email = '$email'";
    $responseData = insert_update_row($sql);
    if(!$responseData['error']){
        $responseData['message'] = "$gcm_registration_id          $email";
    }
        $response->getBody()->write(json_encode($responseData));

});

$app->post('/api/test',function(Request $request, Response $response){
 $file = $request->getParam('name');
      
        $responseData = updateImage('vikash@gmail.com',$file);

$response->getBody()->write(json_encode($responseData));

});
function update_test($name){
    $responseData = array();
       $sql = "UPDATE user SET name = '$name' WHERE email = 'vikash@gmail.com'";
    try{
        // Get DB Object
        $db = new db();
        // Connect
        $db = $db->connect();
        $stmt = $db->prepare($sql);
        $stmt->execute();
        $db = null;
        $responseData["error"] = false;
            $responseData["message"] = $sql;
        return $responseData; //"true";
            }catch(PDOException $ex){
            $response["error"] = true;
            $response["message"] = $ex->getMessage();
        return $responseData;
    }  
}
?>
