<?php
include 'vars.php';

header ( 'Content-Type: application/json' );

$raggio = $_GET ["r"];
$latitudine = $_GET ["lat"];
$longitudine = $_GET ["long"];

if ($raggio != null && $latitudine != null && $longitudine != null) {
	
	$conn = mysqli_connect ( $host, $user, $password, $db_name );
	$query = "select * from monopattini";
	
	// Check connection
	if (mysqli_connect_errno ()) {
		$post_error = json_encode ( array (
				'err' => "Failed to connect to MySQL: " . mysqli_connect_error () 
		), JSON_FORCE_OBJECT );
		echo $post_error;
	}	
	$provaCazzo = array();
	$id_array = array ();
	$lat_array = array ();
	$lon_array = array ();
	$statoBatteria_array = array ();
	$result = mysqli_query ( $conn, $query );
	while ( $row = mysqli_fetch_array ( $result ) ) {
		$idMonopattino = $row ['id_monopattino'];
		$lattmp = $row ['latitudine'];
		$lontmp = $row ['longitudine'];
		$statoBatteria = $row ['stato_batteria'];
		$distance = (3958 * 3.1415926 * sqrt ( ($lattmp - $latitudine) * ($lattmp - $latitudine) + cos ( $lattmp / 57.29578 ) * cos ( $latitudine / 57.29578 ) * ($lontmp - $longitudine) * ($lontmp - $longitudine) ) / 180);
		if ($distance <= $raggio) {
				$id_array [] = $idMonopattino;
				$lat_array [] = $lattmp;
				$lon_array [] = $lontmp;
				$statoBatteria_array [] = $statoBatteria;
			}
	}
	
	$post_data = json_encode ( array (
			'id' => $id_array,
			'lat' => $lat_array,
			'lon' => $lon_array,
			'batteria' => $statoBatteria_array
	), JSON_FORCE_OBJECT );
	echo $post_data;
} else {
	$post_error = json_encode ( array (
			'err' => 'Parametri mancanti!' 
	), JSON_FORCE_OBJECT );
	echo $post_error;
}
?>