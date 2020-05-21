<?php
include 'vars.php';
header ( 'Content-Type: application/json' );

$idUtente= $_GET['idU'];
$idMonopattino = $_GET['idM'];
$idNoleggio = $_GET['idN'];
$importo = $_GET['amount'];
$durataTotale = $_GET['time'];

if ( $idUtente != null && $idMonopattino != null && $idNoleggio != null )
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
	$query = "UPDATE noleggi SET durata_totale = '$durataTotale', importo = '$importo' 
			  WHERE id_noleggio = '$idNoleggio' AND id_utente = '$idUtente' AND id_monopattino='$idMonopattino' AND stato = 0";
	$result = mysqli_query ( $conn, $query );
	
	if ( $result )
	{
		$queryCheckWallet = "SELECT saldo from utenti WHERE id_utente = '$idUtente'";
		$resultCheckWallet = mysqli_query ( $conn, $queryCheckWallet );
		$number = mysqli_fetch_array ( $resultCheckWallet );
		if ( $number != null )
			$saldoUtente = $number ['saldo'];
		else
			$saldoUtente = 0;
		
		//Vedo se l'utente può permettersi il pagamento
		if ( $saldoUtente >= $importo )
		{
			$resto = $saldoUtente - $importo;
			$queryAggiornaSaldo = "UPDATE utenti SET saldo = '$resto' WHERE id_utente = '$idUtente'";
			$resultAggiornaSaldo = mysqli_query ( $conn, $queryAggiornaSaldo );
			
			$queryRegistraTransazione = "INSERT INTO transazioni (id_noleggio, importo) VALUES ('$idNoleggio', '$importo')";
			$reesultRegistrazioneTransazione = mysqli_query ( $conn, $queryRegistraTransazione );
			
			if ( $resultAggiornaSaldo && $reesultRegistrazioneTransazione )
			{
				$post_data = json_encode ( array (
					'pagamentoCompletato' => true
				), JSON_FORCE_OBJECT );
				echo $post_data;
			}
			else
			{
				$post_data = json_encode ( array (
					'pagamentoCompletato' => false
				), JSON_FORCE_OBJECT );
				echo $post_data;
			}
		}
		else
		{
			$resto = $importo - $saldoUtente;
			$post_data = json_encode ( array (
					'pagamentoCompletato' => false,
					'creditoInsufficiente' => true,
					'denaroMancante' => $resto
				), JSON_FORCE_OBJECT );
				echo $post_data;
		}
	}
	else
	{
		$post_data = json_encode ( array (
				'pagamentoCompletato' => false
				), JSON_FORCE_OBJECT );
		echo $post_data;		
	}
	mysqli_close ( $conn );
}
else
{
	$post_error = json_encode ( array (
			'err' => 'Parametri mancanti!' 
	), JSON_FORCE_OBJECT );
	echo $post_error;
}
?>