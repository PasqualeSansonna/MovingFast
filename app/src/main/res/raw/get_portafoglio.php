<?php
// restituisce il portafoglio dato l'id utente
include 'vars.php';

header ( 'Content-Type: application/json' );

$idutente = $_GET ["id"];

if ($idutente != null) {
	$conn = mysqli_connect ( $host, $user, $password, $db_name );
	$query = "select saldo from utenti where id_utente='$idutente'";
	
	// Check connection
	if (mysqli_connect_errno ()) {
		$post_error = json_encode ( array (
				'err' => "Failed to connect to MySQL: " . mysqli_connect_error () 
		), JSON_FORCE_OBJECT );
		echo $post_error;
	}
	
	$result = mysqli_query ( $conn, $query );
	$number = mysqli_fetch_array ( $result );
	
	$post_data = json_encode ( array (
			'saldo' => $number ['saldo']
	), JSON_FORCE_OBJECT );
	
	echo $post_data;
	mysqli_close ( $conn );
} else {
	$post_error = json_encode ( array (
			'err' => 'Parametri mancanti!' 
	), JSON_FORCE_OBJECT );
	echo $post_error;
}

?>