package com.goreecloud.youtubeplayer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.goreecloud.youtubeplayer.domain.ProviderCapability
import com.goreecloud.youtubeplayer.provider.CapabilityResolver
import com.goreecloud.youtubeplayer.provider.local.LocalDemoProvider

private const val GLAZE_TARGET = "1.4.0"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoreeCloudYouTubePlayerApp() {
    val provider = remember { LocalDemoProvider() }
    val resolver = remember { CapabilityResolver() }
    val decisions = remember(provider, resolver) {
        ProviderCapability.entries.map { capability ->
            resolver.resolve(provider, capability)
        }
    }

    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("GoreeCloud YouTube Player Dev") },
                )
            },
        ) { contentPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Native foundation",
                            style = MaterialTheme.typography.headlineMedium,
                        )
                        Text(
                            text = "Provider: ${provider.displayName}",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Text(
                            text = "Glaze UI target: $GLAZE_TARGET — conformance pending",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(
                            text = "This development provider uses no network access and exists only to validate GoreeCloud-owned architecture.",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }

                items(decisions) { decision ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(
                                text = decision.capability.name.replace('_', ' '),
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                text = decision.state.name.replace('_', ' '),
                                style = MaterialTheme.typography.labelLarge,
                            )
                            Text(
                                text = decision.reason,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
            }
        }
    }
}
