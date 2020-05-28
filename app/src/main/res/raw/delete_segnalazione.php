<?php
// elimina la segnalazione dato l'id
include 'vars.php';

header ( 'Content-Type: application/json' );

$id_segnalazione = $_GET ["id"];



if ($id_segnalazione != null) {
	$conn = mysqli_connect ( $host, $user, $password, $db_name );
	$query = "DELETE from segnalazioni  WHERE id_segnalazione = '$id_segnalazione'";

	// Check connection
	if (mysqli_connect_errno ()) {
		$post_error = json_encode ( array (
				'err' => "Failed to connect to MySQL: " . mysqli_connect_error ()
		), JSON_FORCE_OBJECT );
		echo $post_error;
	}

	mysqli_query ( $conn, $query );

	$post_data = json_encode ( array (
	    'ok' => true
    ), JSON_FORCE_OBJECT );

	echo $post_data;
	mysqli_close ( $conn );

}else {

	$post_error = json_encode ( array (
	'err' => 'Parametri mancanti!'
	), JSON_FORCE_OBJECT );
	echo $post_error;
	}

?>