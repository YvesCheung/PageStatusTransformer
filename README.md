# PageStatusTransformer

> A low invasive state management on Android

![Preview](https://github.com/YvesCheung/PageStatusTransformer/blob/master/art/PageStatusTransformer.gif?raw=true)

[中文版](https://github.com/YvesCheung/PageStatusTransformer/blob/master/README_CN.md)

## Feature

- Declare your own state

```kotlin
val transformer =
    PageStatusTransformer.newInstance {

        "Normal" { 
            SimpleState(contentView)
        }

        "Loading" {
            MyLoadingPageState()
        }

        "Empty" {
            MyEmptyPageState()
        }

        //Can be any string or enum
        "Error" {
            MyErrorPageState()
        }
    }
    
//transform to your state by its name
transformer.transform("Empty")
//or by enum
transformer.transform(YourCustomEnum.State1)
```

- Replace the origin `View`
```kotlin
PageStatusTransformer.newInstance(
    replaceTo = contentView /*contentView would be replaced by the new ui which has the same size and parent */
) { 
    State1 {...}
    State2 {...}
}
```
The `view` of the original page can be specified by the optional parameter `replaceTo`. 
When a transform occurs, the `view` in the new state will dynamically replace the `view` in the old state.

Therefore, there is no need to put a so-called `<StatusLayout />` in XML, and it is not necessary to 
plug the business into `BaseActivity` or `BaseFragment`.

- Different ways to toggle the visibility of View
```kotlin
PageStatusTransformer.newInstance(...) { 
    State1 {
        SimpleStatus(view) 
    }
    State2 {
        ViewStubStatus(viewStub)
    }
    State3 {
        ReplacementViewStatus(view)
    }
}
```

`SimpleStatus` supports only changing the visibility of the state UI, which is applicable to the state 
that exists in the layout for a long time, such as the normal state.

`ViewStubStatus` which supports rendering only when used, is suitable for states in lazy loading layouts.

`ReplacementViewStatus` supports dynamic adding and removing state UI, which is suitable for temporary 
state existing in layout.

- Custom State
```kotlin
PageStatusTransformer.newInstance(replaceTo = ...) { 
    "State 1" {
        object: PageDisplayStatus {
            override fun showView(){
                //how to show state 1
            }
            
            override fun hideView(){
                //how to hide state 1
            }
        }
    }
    "State 2" {
        object: ReplacementViewStatus() {
            override fun inflateView(inflater: LayoutInflater, parent: ViewGroup): View {
                return inflater.inflate(...., parent, false)
            }
        
            override fun onViewShow(){...}

            override fun onViewHide(){...}
        }
    }
    ...
}
```

## Java support
Adapting Java language to Kotlin DSL:
```Java
PageStatusTransformer transformer = PageStatusTransformer.newInstance(mRecyclerView, 
    new JavaViewStatusBuilder()
        .putStatus("Normal", new SimpleStatus(mRecyclerView))
        .putStatus("Empty", new YouCustomStatus("No Data"))
        .putStatus("Error", new YouCustomStatus("Sorry! An error happen"))
        .putStatus("Loading"，new YouCustomStatus("Loading"))
        .build()
);


transformer.transform("Empty");
```

## 安装
[![](https://jitpack.io/v/YvesCheung/PageStatusTransformer.svg)](https://jitpack.io/#YvesCheung/PageStatusTransformer)
```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
```
dependencies {
    implementation 'com.github.YvesCheung:PageStatusTransformer:1.0.3'
}
```
