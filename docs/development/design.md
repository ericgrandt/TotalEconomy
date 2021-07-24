Total Economy uses the repository pattern. The primary reasoning behind this design pattern is it greatly simplifies unit testing by decoupling the business layer from the data access layer. As well as that, it makes the codebase easier to read and understand.

One important thing to note with Total Economy is that we are not worried about entities being passed between all of the layers as this level of abstraction isn't necessary for a project of this size. Therefore, an entity created in the data access layer is passed to the service layer where it can then be passed to wherever it's needed such as a command or listener. Same thing in the opposite direction.

## Structure

- The **data access layer** is in the **data** package
- The **service layer** is in the **services** package
- The **entities** are in the **domain** package