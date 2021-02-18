// Copyright 2021 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

import com.google.ads.googleads.v6.services.GoogleAdsServiceClient;
import com.google.ads.googleads.v6.services.GoogleAdsServiceSettings;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.UserCredentials;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Issue {

  public static void main(String[] args) throws IOException {
    // Hangs.
    UserCredentials credentials =
        UserCredentials.newBuilder()
            .setClientId("")
            .setClientSecret("")
            .setRefreshToken("this does not exist")
            .build();

    // Fails, but does not hang.
    Credentials fakeCredentials =
        new Credentials() {
          @Override
          public String getAuthenticationType() {
            return "foo";
          }

          @Override
          public Map<String, List<String>> getRequestMetadata(URI uri) throws IOException {
            return new HashMap<>();
          }

          @Override
          public boolean hasRequestMetadata() {
            return false;
          }

          @Override
          public boolean hasRequestMetadataOnly() {
            return false;
          }

          @Override
          public void refresh() throws IOException {}
        };

    GoogleAdsServiceSettings settings =
        GoogleAdsServiceSettings.newBuilder()
            .setTransportChannelProvider(InstantiatingGrpcChannelProvider.newBuilder().build())
            .setCredentialsProvider(FixedCredentialsProvider.create(fakeCredentials))
            // .setCredentialsProvider(() -> credentials)
            .build();

    GoogleAdsServiceClient client = GoogleAdsServiceClient.create(settings);

    System.out.println("issuing request");

    client.search("123", "select foo");

    System.out.println("never reached");
  }
}
