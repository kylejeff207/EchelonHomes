package com.example.echelon.ui.theme.screens.house

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.echelon.data.HouseViewModel
import com.example.echelon.models.HouseModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewHouseScreen(navController: NavController) {
    val houseViewModel: HouseViewModel = viewModel()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        houseViewModel.fetchMyHouses(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Uploaded Houses", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Green,
                    titleContentColor = Color.Black
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (houseViewModel.houses.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "You haven't uploaded any houses yet.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(houseViewModel.houses) { house ->
                        HouseUploaderCard(house, houseViewModel, navController)
                    }
                }
            }
        }
    }
}

@Composable
fun HouseUploaderCard(house: HouseModel, viewModel: HouseViewModel, navController: NavController) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            AsyncImage(
                model = house.imageUrl,
                contentDescription = "House Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Price: KES ${house.housePrice}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Green
                    )
                    Row {
                        IconButton(onClick = {
                            navController.navigate("updatehouse/${house.id}")
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Blue)
                        }
                        IconButton(onClick = {
                            house.id?.let { viewModel.deleteHouse(it, context) }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Location: ${house.houseLocation}", fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Contact: ${house.phoneNumber}", fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ViewHouseScreenPreview() {
    ViewHouseScreen(rememberNavController())
}
