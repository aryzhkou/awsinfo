# AWS Info

## Endpoints
* *GET* <code>/</code> landing page
* *GET* <code>/api/info</code> get aws instance info
* *GET* <code>/api/files/metadata</code> get metadata for all files
* *GET* <code>/api/files/metadata</code?filename=</code> get metadata for last uploaded file
* *GET* <code>/api/files/metadata</code?filename=anyfile.extension</code> get metadata by filename
* *DEL* <code>/api/files/{anyfile.extension}</code> delete file and metadata by filename
* *POST* <code>/api/files/upload</code>  upload file
* *POST* <code>/api/subscription/subscribe</code> subscribe to new uploaded pictures 
```
{
  "email":"someemail"
}
```

* *POST* <code>/api/subscription/unsubscribe</code> unsubscribe new uploaded pictures 
```
{
  "email":"someemail",
  "subscriptionArn":"if not set then will get from cache"
}
```
* <code>/api/files/download?filename=anyfile.extension</code> download file by filename


To run locally:
* Run docker-compose container
> docker-compose -f docker-compose-environment.yml down
* Run springboot application

get db url from environment variables

### Build without tests
> gradlew clean build -x test