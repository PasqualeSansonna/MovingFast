<?php
include 'vars.php';
header ( 'Content-Type: application/json' );
$idUtente= $_GET['idU'];
$idMonopattino = $_GET['idM'];
$idNoleggio = $_GET['idN'];
$latitudine = $_GET['lat'];
$longitudine = $_GET['long'];
$data = $_GET['data'];
$ora = $_GET['ora'];

if ( $idUtente != null && $idMonopattino != null && $latitudine != null && $longitudine  != null && $data != null  && $ora != null )
{
	// Check connection
	$conn = mysqli_connect ( $host, $user, $password, $db_name );
	if (mysqli_connect_errno ()) 
	{
		$post_error = json_encode ( array (
			'err' => "Failed to connect to MySQL: " . mysqli_connect_error () 
		), JSON_FORCE_OBJECT );
		echo $post_error;
	}
	$query = "UPDATE noleggi SET ora_fine='$ora', latitudine_arrivo ='$latitudine', longitudine_arrivo = '$longitudine', stato = 0
				WHERE id_noleggio = '$idNoleggio' AND id_Monopattino = '$idMonopattino' AND data ='$data' AND stato = 1";
	$result = mysqli_query ( $conn, $query );
	if ( $result )
	{
		$post_data = json_encode ( array (
				'risultatoChiusura' => true
			), JSON_FORCE_OBJECT );
			echo $post_data;
	}
	else
	{
		$post_data = json_encode ( array (
				'risultatoChiusura' => false
				), JSON_FORCE_OBJECT );
		echo $post_data;
	}
}
else
{
	$post_error = json_encode ( array (
			'err' => 'Parametri mancanti!' 
	), JSON_FORCE_OBJECT );
	echo $post_error;
}
?>