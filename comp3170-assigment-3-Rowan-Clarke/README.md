[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/Bkh3N7C9)
# COMP3170 25s1 Assignment 3

## Contents
- [COMP3170 25s1 Assignment 3](#comp3170-25s1-assignment-3)
  - [Contents](#contents)
- [Learning Outcomes](#learning-outcomes)
- [Deliverables](#deliverables)
- [Features](#features)
  - [General requirements](#general-requirements)
  - [Debug - Wireframe mode](#debug---wireframe-mode)
  - [Debug - Normals mode](#debug---normals-mode)
  - [Debug - UVs mode](#debug---uvs-mode)
  - [Boat](#boat)
    - [Mesh](#mesh)
    - [Normals](#normals)
    - [UVs \& Textures](#uvs--textures)
    - [Spinning Fan](#spinning-fan)
    - [Movement](#movement)
  - [Height Map](#height-map)
    - [Mesh](#mesh-1)
    - [Normals](#normals-1)
    - [UVs \& Textures](#uvs--textures-1)
    - [Texture blending](#texture-blending)
  - [Water](#water)
    - [Mesh \& Normals](#mesh--normals)
    - [Transparency](#transparency)
    - [Ripples](#ripples)
    - [Fresnel effect](#fresnel-effect)
  - [Lighting](#lighting)
    - [Day](#day)
    - [Night](#night)
  - [Camera](#camera)
- [Documentation](#documentation)
- [Submission](#submission)
- [Marks](#marks)
  - [Rubrics](#rubrics)
    - [Code](#code)
    - [Documentation](#documentation-1)

# Learning Outcomes

* **ULO 1**: Understand the fundamentals of vector geometry and employ them in devising algorithms to achieve a variety of graphic effects.  
* **ULO 2**: Program 2D and 3D graphical applications using OpenGL embedded in a programming language (such as OpenGL in Java)
* **ULO 3**: Apply vector geometry to implement and combine 3D transformations including rotation, translation, scale and perspective. 
* **ULO 4**: Program vertex and fragment shaders to implement effects such as lighting, texturing, shadows and reflections. 

This assignment covers the following topics:
* 3D modelling with triangular meshes
* 3D Transformations
* Perspective & Orthographic cameras
* Viewport & Scissor rectangles
* Fragment shaders
* Illumination and shading
* Texturing

# Deliverables

You are required to submit:
* An Eclipse project based on the framework provided in this repository, implementing the features listed below.
* A report, based on the Report.md template provided in this repository, describing the implementation of these features.

# Features

Your project will implement a 3D scene showing a hovercraft around a set of islands. 

Your mark will be based on the features you implement, from the table below. Each feature has a mark value attached. The more challenging elements are marked with an asterisk*. 

Some features include a documentation requirement, which should be included in your [report](#documentation) (below). Documentation requirements are marked separately.

Features are described in detail below. Example screenshots & video will be provided on iLearn.

| Feature | Marks |
|---------|-------|
| General requirements | 3% |
| Debug - Wireframe mode | 3% |
| Debug - Normals mode | 3% |
| Debug - UV mode | 3% |
| Boat - Mesh | 3% |
| Boat - Normals | 3% |
| Boat - UVs & Textures | 4% |
| Boat - Spinning Fan | 4% |
| Boat - Movement | 4% |
| Height Map - Mesh | 4% |
| Height Map - Normals | 4% |
| Height Map - UVs & Textures | 4% |
| Height Map - Texture blending | 4% |
| Water - Mesh & Normals | 2% |
| Water - Transparency | 4% |
| Water - Ripples | 4% |
| Water - Fresnel effect | 4% |
| Lighting - Sun | 8% |
| Lighting - Headlamp | 8% |
| Cameras - Third-person | 4% |
| **Total** | 80% |

## General requirements

Your scene should be implemented using:
* Anti-aliasing using 4x multisampling.
* Backface culling and Depth Testing should be implemented
* Mipmaps for all textures (with trilinear filtering)
* Gamma correction (with a default gamma of 2.2)

World space should be oriented so that the the j axis (i.e. the y coordinate) points upwards. The directions of the i and k axes can be set as you deem appropriate. For all world-unit calculations, 1 unit in world space should be equal to 1 metre. 

## Debug - Wireframe mode
* Pressing ‘B’ should toggle between filled and wireframe views of all the meshes in the scene. 

## Debug - Normals mode
* Pressing ‘N’ should turn on a debug mode in which all objects are shaded to display their normals in world coordinates (using a normal matrix), where the RGB colour values are equal to the normal coordinates (r,g,b) = (n_x,n_y,n_z).
* Pressing 'V' should return to the usual non-debug mode.

## Debug - UVs mode
* Pressing ‘M’ should turn on a debug mode in which all objects are shaded to display their UVs, where the RGB colour values are equal to the UV coordinates (r,g,b) = (u,v,0). Values outside the (0,1) range should be wrapped using the [mod](https://docs.gl/sl4/mod) operator.
* Pressing 'V' should return to the usual non-debug mode.

## Boat 

The boat model is provided as a Wavefront OBJ file `models/boat.obj`. This includes three submeshes:
* `boat` contains the hull and frame of the boat
* `fan` contains the fan blades at the back of the boat
* `lantern` cotains a lamp mounted near the front of the boat.

Each submesh includes vertex positions, normals and UVs. An example `Boat.java` class has been provided to illustrate how to access this mesh data of one of these sub-meshes using the `MeshData.java` class provided.

### Mesh
* Extend the `Boat.java` class to draw all three submeshes.
* The boat should initially be drawn at the centre of the map, so that it sits on the water, with part of the hull underneath the surface.

### Normals
* Load the normals from each submesh of the boat and correctly pass them to the shader.

### UVs & Textures

* Texture coordinates (UVs) for the boat are specified in the mesh. You should use these coordinates to texture each part using the `textures/boat.png` texture provided.

### Spinning Fan

* The `fan` submesh should be animated to spin appropriately around its forward axis.

**Note**: As a submesh, the fan's vertices are defined in the same model coordinate frame as the rest of the boat. The centre of the fan is at (0, 1.252717393f, -1.135f) in model coordinates.

### Movement

The boat should be controlled using the WASD keys:
* Holding 'W' and 'S' should cause the boat to move forwards / backwards.
* Holding 'A' and 'D' should cause the boat to turn left / right.

## Height Map

The islands should be generated using the height map image `maps/islands.png`. Each pixel in the image represents the terrain height at a particular point in x/z coordiantes. Height values vary from 0 to 1. A template `HeighMap.java` class has been provided to read the data from this image file.

### Mesh

* Constuct a height map mesh based on this data. The mesh structure should match the examples given in the week 5 lecture.
* Scale the mesh so the terrain is 100m by 100m. The height of the mesh should be scaled so the a height value of 1 corresponds to a terrain height of 50m. 
* **Document**: Illustrate how you construct the height map mesh, including both the vertex positions and index buffer, using a 3x3 example.

### Normals

* Vertex normals should be calculated for each vertex in the mesh, following the method discussed in the week 9 workshop. 
* **Document**: Explain how you calculate the normal for one vertex in your mesh. Provide an appropriate diagram as well as the relevant equations used in the calculation.

### UVs & Textures

* Appropriate vertex UVs should be assigned for each vertex in the mesh.
* One unit of texture space should map to 1m of world space.
* The `terrain-grass.png` texture should be used to colour the mesh.

### Texture blending

* Parts of the terrain below the water level should be textured using the `terrain-sand.png` texture.
* At the water level, there should be a smooth transition from sand to grass.

## Water

### Mesh & Normals

* A flat water mesh should be created as a single quad that covers the entire 100m x 100m terrain, at a height of 20m.
* The mesh should include appropriate normal vectors.
* The mesh should be coloured a suitable shade of cyan (blue-green).

### Transparency

* The water mesh should be semi-transparent, so underwater objects are visible.

### Ripples

* Fragment normals for the water should be calculated in the shader to implement a ripple effect that affect lighting (below). 
* This should be implemented without adjusting vertex positions or normals.
* Ripples should be animated to move at constant speed across the surface of the water.
* **Document**: Explain how this normal is calculated for one fragment. Provide an appropriate diagram as well as the relevant equations used in the calculation.

### Fresnel effect

In real-world physics, surfaces are more reflective when viewed at oblique angles and more transparent when viewed straight on. This effect is described by the [Fresnel equations](https://en.wikipedia.org/wiki/Fresnel_equations), but they are rather complicated. We can simulate a similar effect using the equation:

$$t = \max(0,1 - \hat{v}.\hat{n})$$

where $v$ is the view direction and $n$ is the fragment normal. 

* Use this equation to linearly interpolate between suitable minimum and maximum alpha values on the water mesh, so it is transparent if you look straight down, but opaque if you look along the surface.

## Lighting

The project should implement day and night modes:
* Pressing the 'M' key should switch between day and night modes.

Each light mode should implement:
* Ambient lighting on all meshes.
* Diffuse lighting on all meshes.
* Specular lighting on the water surface.

Ambient light levels in each mode should be high during the day and low at night.

### Day

In day mode:
* The sky should be blue.
* Ambient light should be high.
* Sunlight should be colour white.
* Pressing the '[' and ']' keys should rotate the direction of the sun from east to west.
* Lighting should use a directional light source based on the position of the sun.
* **Document**: Explain how the day-time lighting value for a point on the height map is calculated, using the third-person camera. Provide an appropriate diagram as well as the relevant equations used in the calculation.

### Night

In night mode,
* The sky should be black.
* Ambient light should be low (but not zero).
* Lighting should use a point light source based centred at the lamp mounted on the boat at position (-0.78, 1.39, 0.58) in model coordinates.
* Lamplight should be coloured yellow.
* The light should only affect objects in front of the light source, in the direction it is facing. 
* Pressing the '[' and ']' keys should rotate the lamp left and right.
* **Document**: Explain how the night-time lighting value for a point on the height map is calculated, using the third-person camera. Provide an appropriate diagram as well as the relevant equations used in the calculation.

## Camera

* The camera is a perspective camera that follows the boat from an external point of view.
* The camera should always face towards the boat’s position in world space and maintain a constant distance from the boat’s origin.
* The arrow keys should control the yaw and pitch of the camera:
    - Pressing left and right should rotate the camera clockwise and anticlockwise around the boat, respectively.
    - Pressing up and down should pitch the camera up and down, to a maximum of plus or minus 90 degrees (i.e straight up or straight down).
* Pressing the 'Page up' and 'Page down' keys should dolly the camera forwards and back (i.e. change the distance of the camera from the boat) between sensible minimum and maximum values. 
* Pressing the ',' (comma) and '.' (period) keys should zoom the camera in and out (i.e. change the field of view of the camera) between sensible minimum and maximum values. 
* Resizing the window should change the aspect of the camera view volume to match.
* Near and far planes should be set so the entire boat is visible, as well as some of the surrounding landscape.
* **Document**: Illustrate how you calculate the position and view volume of the third-person camera.

# Documentation

You should complete the `Report.md` template included in the project. The report should include:
* A completed table indicating the features you have attempted
* An illustration of the scene graph used.
* Responses to each of the documentation requirements for the features described above.
  
Documentation is marked separately from implementation, but should reflect the approach taken in your code. You can attempt documentation questions for features you have not implemented or completed, but should indicate that this is the case.

Documentation should include both diagrams and relevant equations to explain your solution. **Note**: Merely copying images from the lecture notes or other sources will get zero marks (and may be treated academic misconduct).

Document marks will be assigned per-question as:

| Feature | Marks |
|---------|-------|
| Scene graph | 2% |
| Height Map - Mesh | 3% |
| Height Map - Normals | 3% |
| Water - Ripples | 3% |
| Lighting - Sun | 3% |
| Lighting - Headlamp | 3% |
| Cameras - Third-person | 3% |
| **Total** | 20% |

# Submission

Both the Eclipse project and report will be submitted using this GitHub repository. Make sure to commit and push your work before the submission deadline. Any commits made after this deadline will be treated as late and standard late penalties will be applied (see the COMP3170 Unit Guide for details). Only work in the `main` branch of your repository will be marked. 

# Marks

Each of the above components will be marked on the rubric below. The total sum of these marks will give you your mark out of 100 for the task (80 for code, 20 for documentation). Marks will not be awarded for elements not meaningfully implemented.

## Rubrics

### Code
Each feature attempted by you will be marked using the rubric below.
|Criteria|Grade|Description|
|-|-|-|
|Correctness|HD (100)|Code relevant to feature is free from any apparent errors. Problems are solved in a suitable fashion. Contains no irrelevant code.|
||D (80)|Code relevant to feature has minor errors which do not significantly affect performance. Contains no irrelevant code.|
||CR (70)|Code relevant to feature has one or two minor errors that affect performance. Problems may be solved in ways that are convoluted or otherwise show lack of understanding. Contains some copied code that is not relevant to the problem.|
||P (60)|Code relevant to feature is functional but contains major flaws. Contains large passages of copied code that are not relevant to the problem.|
||F (0-40)|Code relevant to feature compiles and runs, but major elements are not functional.|
|Clarity|HD (100)|Good consistent style. Well structured & commented code relevant to feature. Appropriate division into classes and methods, to make implementation clear.|
||D (80)|Code relevant to feature is readable with no significant code-smell. Code architecture is adequate but could be improved.|
||CR (70)|Code relevant to feature is readable but has some code-smell that needs to be addressed. Code architecture is adequate but could be improved.|
||P (60)|Significant issues with quality of code relevant to feature. Inconsistent application of style. Poor readability with code-smell issues. Code architecture could be improved.|
||F (0-40)|Significant issues with quality of code relevant to feature. Inconsistent application of style. Poor readability with code-smell issues. Messy code architecture with significant encapsulation violations.|

### Documentation
Each component of your documentation will be marked using the rubric below.

|Grade|Description|
|-|-|
|HD (100)|Illustrations are neat, clear and well annotated. Relevant equations are provided and clearly annotated. No discrepancies between explanation and code (except as noted).|
|D (80)|Illustrations are neat and clear. Relevant equations are provided. No discrepancies between explanation and code (except as noted).|
|CR (70)|Minor sloppiness or missing detail. Equations are provided but include minor inaccuracies. Minor discrepencies between documentation and code.|
|P (60)|Significant sloppiness or missing detail. Equations are provided but include major inaccuracies. Values in illustrations show understanding of task, but may not reflect code.|
|F (0-40)|Illustrations are unclear and badly drawn. Does not make use of graph paper. Equations are not provided or are not relevant to explanation.|

