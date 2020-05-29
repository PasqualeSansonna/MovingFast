<?php
include 'vars.php';

header ( 'Content-Type: application/json' );

$raggio = $_GET ["r"];
$latitudine = $_GET ["lat"];
$longitudine = $_GET ["long"];

if ($raggio != null && $latitudine != null && $longitudine != null) {

    $conn = mysqli_connect ( $host, $user, $password, $db_name );
    $query = "SELECT monopattini.id_monopattino, monopattini.latitudine, monopattini.longitudine, monopattini.stato_batteria, monopattini.richiesta_manutenzione,
	                 segnalazioni.id_monopattino, segnalazioni.id_segnalazione, segnalazioni.guasto_freni, segnalazioni.guasto_ruote, segnalazioni.guasto_manubrio,
	                 segnalazioni.guasto_acceleratore, segnalazioni.guasto_blocco, segnalazioni.guasto_altro
	          FROM monopattini INNER JOIN segnalazioni
	          WHERE monopattini.id_monopattino = segnalazioni.id_monopattino";

    // Check connection
    if (mysqli_connect_errno ()) {
        $post_error = json_encode ( array (
            'err' => "Failed to connect to MySQL: " . mysqli_connect_error ()
        ), JSON_FORCE_OBJECT );
        echo $post_error;
    }

    $id_array = array ();
    $lat_array = array ();
    $lon_array = array ();
    $statoBatteria_array = array ();
    $id_segn_array  = array ();
    $freni_array = array ();
    $ruote_array = array ();
    $manubrio_array = array ();
    $acceleratore_array = array ();
    $blocco_array = array ();
    $altro_array = array ();
    $result = mysqli_query ( $conn, $query );
    while ( $row = mysqli_fetch_array ( $result ) ) {
        $idMonopattino = $row ['id_monopattino'];
        $lattmp = $row ['latitudine'];
        $lontmp = $row ['longitudine'];
        $statoBatteria = $row ['stato_batteria'];
        $idSegnalazione = $row ['id_segnalazione'];
        $segnalazioneFreni = $row ['guasto_freni'];
        $segnalazioneRuote = $row ['guasto_ruote'];
        $segnalazioneManubrio = $row ['guasto_manubrio'];
	$segnalazioneAcceleratore = $row ['guasto_acceleratore'];
	$segnalazioneBlocco = $row ['guasto_blocco'];
	$segnalazioneAltro = $row ['guasto_altro'];
        $distance = (3958 * 3.1415926 * sqrt ( ($lattmp - $latitudine) * ($lattmp - $latitudine) + cos ( $lattmp / 57.29578 ) * cos ( $latitudine / 57.29578 ) * ($lontmp - $longitudine) * ($lontmp - $longitudine) ) / 180);
        if ($distance <= $raggio) {
            $id_array [] = $idMonopattino;
            $lat_array [] = $lattmp;
            $lon_array [] = $lontmp;
            $statoBatteria_array [] = $statoBatteria;
            $id_segn_array [] = $idSegnalazione;
            $freni_array[] = $segnalazioneFreni;
            $ruote_array [] = $segnalazioneRuote;
            $manubrio_array [] = $segnalazioneManubrio;
	    $acceleratore_array [] = $segnalazioneAcceleratore;
	    $blocco_array [] = $segnalazioneBlocco;
	    $altro_array [] = $segnalazioneAltro;
        }
    }

    $post_data = json_encode ( array (
        'idMonopattino' => $id_array,
        'lat' => $lat_array,
        'lon' => $lon_array,
        'batteria' => $statoBatteria_array,
        'idSegnalazione' => $id_segn_array,
        'freni' => $freni_array,
        'ruote' => $ruote_array,
        'manubrio' => $manubrio_array,
	'acceleratore'=> $acceleratore_array,
	'blocco'=> $blocco_array,
	'altro'=> $altro_array
    ), JSON_FORCE_OBJECT );
    echo $post_data;
} else {
    $post_error = json_encode ( array (
        'err' => 'Parametri mancanti!'
    ), JSON_FORCE_OBJECT );
    echo $post_error;
}
?>