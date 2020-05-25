<?php
include 'vars.php';

header ( 'Content-Type: application/json' );

$raggio = $_GET ["r"];
$latitudine = $_GET ["lat"];
$longitudine = $_GET ["long"];

if ($raggio != null && $latitudine != null && $longitudine != null) {

    $conn = mysqli_connect ( $host, $user, $password, $db_name );
    $query = "SELECT monopattini.id_monopattino, monopattini.latitudine, monopattini.longitudine, monopattini.stato_batteria, monopattini.richiesta_manutenzione,
	                 segnalazioni.id_monopattino, segnalazioni.id_segnalazione, segnalazioni.guasto_fregni, segnalazioni.guasto_ruote, segnalazioni.guasto_manubrio,
	                 segnalazioni.descrizione
	          FROM monopattini, segnalazioni 
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
    $descrizione_array = array ();
    $result = mysqli_query ( $conn, $query );
    while ( $row = mysqli_fetch_array ( $result ) ) {
        $idMonopattino = $row ['id_monopattino'];
        $lattmp = $row ['latitudine'];
        $lontmp = $row ['longitudine'];
        $statoBatteria = $row ['stato_batteria'];
        $idSegnalazione = $row ['id_segnalazione'];
        $segnalazioneFreni = $row ['guasto_fregni'];
        $segnalazioneRuote = $row ['guasto_ruote'];
        $segnalazioneManubrio = $row ['guasto_manubrio'];
        $segnalazioneDescrizione = $row ['descrizione'];
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
            $descrizione_array [] = $segnalazioneDescrizione;
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
        'descrizioneSegnalazione' => $descrizione_array
    ), JSON_FORCE_OBJECT );
    echo $post_data;
} else {
    $post_error = json_encode ( array (
        'err' => 'Parametri mancanti!'
    ), JSON_FORCE_OBJECT );
    echo $post_error;
}
?>