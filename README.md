# AWS Info

## Endpoints
* *GET* <code>/</code> landing page
* *GET* <code>/api/info</code> get aws instance info
* *GET* <code>/api/files/metadata</code> get metadata for all files
* *GET* <code>/api/files/metadata</code?filename=</code> get metadata for last uploaded file
* *GET* <code>/api/parameter?param=px16&decrypt=true</code> to get parameter and decrypt it if necessary (AWS SSM is used)
* *POST* <code>/api/parameter</code>
```
{
    "key": "2-255 length String, required",
    "value": "2-255 length String, required",
    "secured": "true", //[boolean] true if SecureString type is needed, false by default, optional
    "overwrite": "true" //[boolean] true if parameter could be overrided, false by default, optional
}
```
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