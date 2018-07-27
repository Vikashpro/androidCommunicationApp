<?php

			define("GOOGLE_API_KEY", "AAAAZ1QOHao:APA91bHAmV8xRXKmGUiCbdvfi6I8amfNcNxoKu-ADoO8k0Z8yGTGLtEitXQASLgAcFBQlfN1fP9BAom-BvBGQHDl9Io1dxWQSO9kHyCerSYKVdBhd8o6CXimKOFRrcAG-i8ZOiLg0QKM");
 
// push notification flags
		 define('PUSH_FLAG_CHATROOM', 1);    #1
		 define('PUSH_FLAG_USER', 2);

	class db{
		//Properties
		private $dbhost = 'localhost';
		private $dbuser = 'root';
		private $dbpass = '';
		private $dbname = 'online_notice';

		#2

		//Connect
		public function connect(){
			$mysql_connect_str = "mysql:host=$this->dbhost;dbname=$this->dbname";
			$dbConnection = new PDO($mysql_connect_str,$this->dbuser,$this->dbpass);
			$dbConnection->setAttribute(PDO::ATTR_ERRMODE,PDO::ERRMODE_EXCEPTION);
			return $dbConnection;
		}
	}
?>