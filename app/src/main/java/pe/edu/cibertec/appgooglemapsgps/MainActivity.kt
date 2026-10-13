package pe.edu.cibertec.appgooglemapsgps

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import pe.edu.cibertec.appgooglemapsgps.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() , OnMapReadyCallback {

    private lateinit var binding : ActivityMainBinding
    private lateinit var map: GoogleMap
    // Declarar el cliente de ubicación
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Registrador para solicitar el permiso de ubicación
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permiso concedido, activamos la ubicación
            enableMyLocation()
        } else {
            Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Inicializar el cliente de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        // Obtener el SupportMapFragment y notificar cuando el mapa esté listo
        val mapFragment = supportFragmentManager
            .findFragmentById(binding.map.id) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0
        /*// 1. Definir una coordenada (Ejemplo: Plaza Mayor de Lima, Perú)
        val lima = LatLng(-12.046374, -77.029928)
        // 2. Añadir un marcador en esa coordenada
        map.addMarker(
            MarkerOptions()
                .position(lima)
                .title("Plaza Mayor de Lima")
                .snippet("Centro histórico del Perú")
        )
        // 3. Mover la cámara del mapa hacia el marcador con un nivel de zoom de 15f
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(lima, 15f))
        // Configuraciones adicionales opcionales
        map.uiSettings.isZoomControlsEnabled = true // Muestra botones de + y -
        */
        // Cuando el mapa carga, verificamos los permisos
        checkLocationPermission()
    }


    private fun checkLocationPermission() {
        // Verificar si ya tenemos el permiso
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            enableMyLocation()
        } else {
            // Solicitar el permiso al usuario
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun enableMyLocation() {
        // Se requiere un chequeo redundante por seguridad de Android
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Esto añade el "punto azul" y el botón para centrar en el GPS
            map.isMyLocationEnabled = true
            map.uiSettings.isMyLocationButtonEnabled = true
            // Obtener la última ubicación y centrar la cámara
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)

                    // Hacer un zoom suave hacia la ubicación actual (15f es un buen nivel para ver calles)
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                } else {
                    Toast.makeText(this, "No se pudo obtener la ubicación actual", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}