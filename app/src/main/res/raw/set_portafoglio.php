<?php
// setta il portafoglio dato l'id utente ed i soldi, aggiungendo al portafoglio corrente
include 'vars.php';

header ( 'Content-Type: application/json' );

$idutente = $_GET ["id"];
$soldi = $_GET ["soldi"];

if ($idutente != null && $soldi != null) {
	$conn = mysqli_connect ( $host, $user, $password, $db_name );
	
	// Check connection
	if (mysqli_connect_errno ()) {
		$post_error = json_encode ( array (
				'err' => "Failed to connect to MySQL: " . mysqli_connect_error () 
		), JSON_FORCE_OBJECT );
		echo $post_error;
	}
	
	$soldi_correnti_query = "select saldo from utenti where id_utente='$idutente'";
	$soldi_correnti_result = mysqli_fetch_array ( mysqli_query ( $conn, $soldi_correnti_query ) );
	$soldi_correnti = $soldi_correnti_result ['saldo'];
	if ($soldi_correnti == null)
		$soldi_correnti = 0;
	$soldi_aggiornati = $soldi_correnti + $soldi;
	
	$query = "update utenti set saldo = '$soldi_aggiornati' where id_utente='$idutente'";
	
	mysqli_query ( $conn, $query );
	
	$post_data = json_encode ( array (
			'ok' => true 
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