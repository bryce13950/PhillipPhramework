<?php

/*
 * use this file to automatically handle uncaught exception reports on the server
 */
 /*
  *	set this variable to the database name
  */
 $dbname = "";


 /*
  *	set this variable to the database User Name
  */
 $dbuser = "";
 /*
  *	set this variable to the database password
  */
 $dbpass = "";

	try{

		$DBH = new PDO('mysql:dbname=' . $dbname .';host=localhost',$dbuser,$dbpass);
		$DBH->setAttribute(PDO::ATTR_ERRMODE,PDO::ERRMODE_EXCEPTION);

		$SQL = "INSERT INTO 
					`android_error` 
					(`message`, `location`, `localized`, `class`, `error_at`, `serial_number`, `android_version`, `application_version`, `online_error`, `extras`)
				VALUES
					(:message, :location, :localized, :class, :error_at, :serial, :android, :application, :online, :extras)";
		$insert = $DBH->prepare($SQL);
		
		$insert->execute(array(
			"message" 		=> isset($_POST["message"]) 			? $_POST["message"] 							: null,
			"location" 		=> isset($_POST["location"]) 			? $_POST["location"] 							: null,
			"localized" 	=> isset($_POST["localized"])			? $_POST["localized"] : null,
			"class" 		=> isset($_POST["class"]) 				? $_POST["class"] 								: null,
			"error_at" 		=> isset($_POST["time"]) 				? date("Y-m-d H:i:s", $_POST["time"] / 1000)	: date("Y-m-d H:i:s"),
			"serial" 		=> isset($_POST["serial"])				? $_POST["serial"] 								: null,
			"android"		=> isset($_POST["android_version"]) 	? $_POST["android_version"] 					: null,
			"application"	=> isset($_POST["application_version"])	? $_POST["application_version": null,
			"online" 		=> isset($_POST["successful"]) 			? $_POST["successful"] 							: null,
			"extras" 		=> isset($_POST["extras"]) 				? $_POST["extras"] 								: null,
		));
		
		$trace = json_decode($_POST['stack_trace']);
		$id = $DBH->lastInsertId();
		foreach($trace as $stack){
		    $class 	=	$stack->{'class'};
		    $file	=	$stack->{'file'};
		    $method	=	$stack->{'method'};
		    $line	=	$stack->{'line'};
		    $SQL = "INSERT INTO `android_stacktrace` (`main_id`,`class`,`file`,`method`,`line`) VALUES (:id, :class, :file, :method, :line)";
		    $IH = $DBH->prepare($SQL);
		    $IH->execute(array(
		    	"id"		=>	$id,
		    	"class"		=>	$stack->class,
		    	"file"		=>	$stack->file,
		    	"method"	=>	$stack->method,
		    	"line"		=>	$stack->line
		    ));
		}

	}catch(Exception $e){

		$headers  = 'MIME-Version: 1.0' . "\r\n";
		$headers .= 'Content-type: text/html; charset=iso-8859-1' . "\r\n";


		$message = "<img src='http://userserve-ak.last.fm/serve/500/50441183/Xzibit.png'/>
					<p>I herd you liek to error, so I put an error in your error so you can error while you error.</p>";
				
		$message .= "<p>".$e->getMessage()."</p>";

		mail("bryce13950@gmail.com", "Yo Dawg", $message, $headers);
		
	}
echo "success";
?>